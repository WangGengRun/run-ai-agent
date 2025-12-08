package com.run.runaiagent.demo.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MultiQueryExpanderDemoTest {

    @Resource
    private MultiQueryExpanderDemo multiQueryExpanderDemo;
    @Test
    void expand() {
        List<Query> queries = multiQueryExpanderDemo.expand("世界上最聪明的人是谁哈哈哈哈哈，不是我吗哈哈哈");
        Assertions.assertNotNull(queries);
    }
}