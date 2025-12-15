package com.run.runaiagent.agent;

import com.run.runaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的基础代理类，用于管理代理状态和执行流程
 *提供状态转换、内存管理和基于步骤的执行循环的基础功能
 * 子类必须实现step方法
 */

@Data
@Slf4j
public abstract class BaseAgent {
    //核心属性
    private String name;
    //提示词
    private String systemPrompt;
    private String nextStepPrompt;
    //代理状态
    private AgentState state=AgentState.IDLE;
    //执行步骤控制
    private int currentStep=0;
    private int maxSteps=10;
    //LLM 大模型
    private ChatClient chatClient;
    //上下文记忆（自主维护）
    private List<Message> messageList = new ArrayList<>();
    /**
     * 运行agent
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt){

        //开始先进行校验
        if(this.state!=AgentState.IDLE){
            throw new RuntimeException("cannot run agent when state is IDLE"+this.state);
        }
        if(StringUtil.isEmpty(userPrompt)){
            throw new RuntimeException("cannot run agent when user prompt is empty");
        }
        //执行，更改状态
        this.state=AgentState.RUNNING;
        //记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> results=new ArrayList<>();

        try{
            //执行循环
            for(int i=0;i<maxSteps&& state!=AgentState.FINISHED;i++){
                int stepNumber=i + 1;
                currentStep=stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);
                //单步执行得到结果
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        }catch(Exception e){
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        }finally {
            //清理资源
            this.cleanup();
        }

    }

    /**
     * 定义单个步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){
        //子类可以重写方法来清理资源
    }


}
