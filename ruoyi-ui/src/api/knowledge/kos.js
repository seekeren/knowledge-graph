import request from '@/utils/request'

export function extract(data) {
  return request({
    url: '/knowledge/kos/extract',
    method: 'post',
    data: data
  })
}

export function refreshKos() {
  return request({
    url: '/knowledge/kos/refresh',
    method: 'post'
  })
}

export function listTerms(query) {
  return request({
    url: '/knowledge/kos/terms',
    method: 'get',
    params: query
  })
}

export function getTerm(termId) {
  return request({
    url: '/knowledge/kos/terms/' + termId,
    method: 'get'
  })
}

export function addTerm(data) {
  return request({
    url: '/knowledge/kos/terms',
    method: 'post',
    data: data
  })
}

export function updateTerm(data) {
  return request({
    url: '/knowledge/kos/terms',
    method: 'put',
    data: data
  })
}

export function delTerm(termId) {
  return request({
    url: '/knowledge/kos/terms/' + termId,
    method: 'delete'
  })
}

export function listRules(query) {
  return request({
    url: '/knowledge/kos/rules',
    method: 'get',
    params: query
  })
}

export function getRule(ruleId) {
  return request({
    url: '/knowledge/kos/rules/' + ruleId,
    method: 'get'
  })
}

export function addRule(data) {
  return request({
    url: '/knowledge/kos/rules',
    method: 'post',
    data: data
  })
}

export function updateRule(data) {
  return request({
    url: '/knowledge/kos/rules',
    method: 'put',
    data: data
  })
}

export function delRule(ruleId) {
  return request({
    url: '/knowledge/kos/rules/' + ruleId,
    method: 'delete'
  })
}
