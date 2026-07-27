package com.ruoyi.web.controller.knowledge;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.knowledge.domain.LlmModelConfig;
import com.ruoyi.system.knowledge.domain.LlmPromptTemplate;
import com.ruoyi.system.knowledge.service.ILlmService;

@RestController
@RequestMapping("/knowledge/llm")
public class LlmExtractionController extends BaseController
{
    @Autowired
    private ILlmService llmService;

    @GetMapping("/models")
    public AjaxResult listModels()
    {
        List<LlmModelConfig> list = llmService.listModels();
        return success(list);
    }

    @GetMapping("/prompts")
    public AjaxResult listPrompts()
    {
        List<LlmPromptTemplate> list = llmService.listPrompts();
        return success(list);
    }

    @GetMapping("/prompts/{promptId}")
    public AjaxResult getPromptDetail(@PathVariable Long promptId)
    {
        LlmPromptTemplate prompt = llmService.getPromptById(promptId);
        return success(prompt);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:llm:extract')")
    @Log(title = "LLM知识抽取", businessType = BusinessType.INSERT)
    @PostMapping("/extract")
    public AjaxResult extract(@Validated @RequestBody LlmExtractRequest request)
    {
        LlmPromptTemplate prompt = llmService.getPromptById(Long.parseLong(request.getPromptId()));
        if (prompt == null)
        {
            return error("提示词模板不存在");
        }

        String result = llmService.callLlm(request.getModel(), prompt.getContent(), request.getContent());

        LlmExtractResponse response = new LlmExtractResponse();
        response.setExtractType(request.getExtractType());
        response.setLanguage(request.getLanguage());
        response.setModel(request.getModel());
        response.setPromptId(request.getPromptId());
        response.setPromptName(prompt.getPromptName());
        response.setResult(result);
        return success(response);
    }

    public static class LlmExtractRequest
    {
        private String extractType = "entity";
        private String language = "zh";
        private String model;
        private String promptId;
        private String content;

        public String getExtractType() { return extractType; }
        public void setExtractType(String extractType) { this.extractType = extractType; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getPromptId() { return promptId; }
        public void setPromptId(String promptId) { this.promptId = promptId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class LlmExtractResponse
    {
        private String extractType;
        private String language;
        private String model;
        private String promptId;
        private String promptName;
        private String result;

        public String getExtractType() { return extractType; }
        public void setExtractType(String extractType) { this.extractType = extractType; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getPromptId() { return promptId; }
        public void setPromptId(String promptId) { this.promptId = promptId; }
        public String getPromptName() { return promptName; }
        public void setPromptName(String promptName) { this.promptName = promptName; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
    }
}
