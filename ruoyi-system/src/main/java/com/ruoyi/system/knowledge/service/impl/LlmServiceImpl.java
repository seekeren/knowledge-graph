package com.ruoyi.system.knowledge.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.knowledge.domain.LlmModelConfig;
import com.ruoyi.system.knowledge.domain.LlmPromptTemplate;
import com.ruoyi.system.knowledge.service.ILlmService;
import com.ruoyi.system.mapper.LlmModelConfigMapper;
import com.ruoyi.system.mapper.LlmPromptTemplateMapper;

@Service
public class LlmServiceImpl implements ILlmService
{
    @Autowired
    private LlmModelConfigMapper modelMapper;

    @Autowired
    private LlmPromptTemplateMapper promptMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<LlmModelConfig> listModels()
    {
        return modelMapper.selectModelList();
    }

    @Override
    public List<LlmPromptTemplate> listPrompts()
    {
        return promptMapper.selectPromptList();
    }

    @Override
    public LlmPromptTemplate getPromptById(Long promptId)
    {
        return promptMapper.selectPromptById(promptId);
    }

    @Override
    public String callLlm(String modelName, String systemPrompt, String userContent)
    {
        List<LlmModelConfig> models = modelMapper.selectModelList();
        LlmModelConfig model = models.stream()
                .filter(m -> m.getModelName().equals(modelName))
                .findFirst()
                .orElseThrow(() -> new ServiceException("模型不存在或未启用"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(model.getApiKey());

        JSONObject body = new JSONObject();
        body.put("model", modelName);
        body.put("temperature", model.getTemperature().doubleValue());
        body.put("max_tokens", model.getMaxTokens());
        body.put("messages", new Object[] {
            new JSONObject().fluentPut("role", "system").fluentPut("content", systemPrompt),
            new JSONObject().fluentPut("role", "user").fluentPut("content", userContent)
        });

        HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);

        try
        {
            ResponseEntity<String> resp = restTemplate.postForEntity(model.getEndpoint() + "/chat/completions", entity, String.class);
            JSONObject json = JSON.parseObject(resp.getBody());
            return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        }
        catch (Exception e)
        {
            throw new ServiceException("LLM调用失败: " + e.getMessage());
        }
    }
}
