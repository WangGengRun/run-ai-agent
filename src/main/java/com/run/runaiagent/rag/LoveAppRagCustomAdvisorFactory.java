package com.run.runaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 创建自定义的RAG检索增强顾问的工厂
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {
    /**
     * 创建自定义的RAG检索增强顾问
     * @param vectorStore
     * @param status
     * @return
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        //过滤特定状态文档，通过元信息进行过滤
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        //创建文档检索器，使用SpringAi的，阿里云DashScope文档检索器目前不支持关联筛选表达式
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                //过滤条件
                .filterExpression(expression)
                //相似度阈值
                .similarityThreshold(0.5)
                //返回文档数量
                .topK(3)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
//                .queryAugmenter()
                .build();
    }
}
