package com.ruoyi.system.knowledge.service;

import java.util.List;
import com.ruoyi.system.knowledge.domain.LlmModelConfig;
import com.ruoyi.system.knowledge.domain.LlmPromptTemplate;

public interface ILlmService
{
    List<LlmModelConfig> listModels();

    List<LlmPromptTemplate> listPrompts();

    LlmPromptTemplate getPromptById(Long promptId);

    String callLlm(String modelName, String systemPrompt, String userContent);
}
