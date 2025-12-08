package com.run.runaiagent;

import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class RunAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunAiAgentApplication.class, args);
    }

}
