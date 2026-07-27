package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.knowledge.domain.LlmModelConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LlmModelConfigMapper
{
    List<LlmModelConfig> selectModelList();

    LlmModelConfig selectModelById(Long modelId);
}
