package com.run.runaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "AI智能体.pdf";
        String content = "这是一个AI智能体";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}