package com.run.runaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();
    static {
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    public  FileBasedChatMemory(String dir) {
        this.BASE_DIR=dir;
        File BaseDir=new File(dir);
        if(!BaseDir.exists()){
            BaseDir.mkdirs();
        }
    }
//    @Override
//    public void add(String conversationId, Message message) {
//        saveConversation(conversationId,List.of(message));
//    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList=getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId,messageList);
    }

    /**
     * 获取消息的倒数几条
     * @param conversationId
     * @param lastN
     * @return
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messageList=getOrCreateConversation(conversationId);
        return messageList.stream()
                .skip(Math.max(0,messageList.size()- lastN))
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        File file=getConversationFile(conversationId);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 获取或创建会话消息的列表
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId){
        File file=getConversationFile(conversationId);
        List<Message> messages=new ArrayList<>();
        if(file.exists()){
            try(Input input=new Input(new FileInputStream(file))) {
                messages=kryo.readObject(input,ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 保存会话消息
     * @param conversationId
     * @param messages
     */
  private void saveConversation(String conversationId,List<Message> messages){
        File file=getConversationFile(conversationId);
        try(Output output=new Output(new FileOutputStream(file))){
            kryo.writeObject(output,messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
  }
    /**
     * 每个会话文件单独保存
     * conversationId 是SpringAI生成的
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId){
        return new File(BASE_DIR,conversationId+".kryo");
    }

}
