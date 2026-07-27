import request from '@/utils/request'

export function llmExtract(data) {
  return request({
    url: '/knowledge/llm/extract',
    method: 'post',
    data: data
  })
}

export function listModels() {
  return request({
    url: '/knowledge/llm/models',
    method: 'get'
  })
}

export function listPrompts() {
  return request({
    url: '/knowledge/llm/prompts',
    method: 'get'
  })
}

export function getPromptDetail(promptId) {
  return request({
    url: `/knowledge/llm/prompts/${promptId}`,
    method: 'get'
  })
}
