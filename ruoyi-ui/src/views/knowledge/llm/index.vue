<template>
  <div class="app-container" style="max-width: 850px; margin: 0 auto;">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">LLM知识抽取</span>
        </div>
      </template>

      <div class="form-section">
        <div class="form-row">
          <span class="label">抽取类型：</span>
          <el-radio-group v-model="form.extractType">
            <el-radio label="entity">实体</el-radio>
            <el-radio label="relation">关系</el-radio>
          </el-radio-group>
        </div>

        <div class="form-row" style="margin-top: 25px;">
          <span class="label">语言类型：</span>
          <el-radio-group v-model="form.language">
            <el-radio label="zh">中文</el-radio>
            <el-radio label="en">英文</el-radio>
          </el-radio-group>
        </div>

        <div class="form-row" style="margin-top: 25px;">
          <span class="label">选择模型：</span>
          <el-select v-model="form.model" style="width: 220px;">
            <el-option
              v-for="m in modelList"
              :key="m.modelId"
              :label="m.displayName"
              :value="m.modelName"
            />
          </el-select>
        </div>

        <div class="form-row" style="margin-top: 25px;">
          <span class="label">提示词：</span>
          <el-select v-model="form.promptId" style="width: 220px;">
            <el-option
              v-for="p in promptList"
              :key="p.promptId"
              :label="p.promptName"
              :value="String(p.promptId)"
            />
          </el-select>
          <el-button
            type="primary"
            link
            style="margin-left: 10px;"
            @click="showPromptPreview"
          >
            预览
          </el-button>
        </div>

        <div style="margin-top: 25px;">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入待抽取文本..."
          />
        </div>

        <div style="margin-top: 25px;">
          <el-button
            type="primary"
            size="large"
            style="width: 100%; height: 50px; font-size: 16px;"
            :loading="loading"
            @click="handleExtract"
          >
            抽取
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card v-if="result" shadow="never" style="margin-top: 20px;">
      <template #header>
        <div class="card-header" style="font-size: 18px; color: #409eff; font-weight: bold;">
          抽取结果
        </div>
      </template>
      <div class="result-content">
        <pre style="white-space: pre-wrap; font-size: 14px; margin: 0;">{{ result.result }}</pre>
      </div>
    </el-card>

    <el-dialog v-model="previewVisible" title="提示词预览" width="600px">
      <div style="max-height: 400px; overflow-y: auto;">
        <pre style="white-space: pre-wrap; font-size: 14px; background: #f5f7fa; padding: 15px; border-radius: 4px;">{{ promptPreviewContent }}</pre>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="LlmExtraction">
import { ref, onMounted } from 'vue'
import { llmExtract, listModels, listPrompts, getPromptDetail } from "@/api/knowledge/llm"

const form = ref({
  extractType: 'entity',
  language: 'zh',
  model: '',
  promptId: '',
  content: ''
})

const loading = ref(false)
const result = ref(null)
const modelList = ref([])
const promptList = ref([])
const previewVisible = ref(false)
const promptPreviewContent = ref('')

onMounted(() => {
  loadModels()
  loadPrompts()
})

function loadModels() {
  listModels().then(res => {
    modelList.value = res.data || []
    if (modelList.value.length > 0 && !form.value.model) {
      form.value.model = modelList.value[0].modelName
    }
  })
}

function loadPrompts() {
  listPrompts().then(res => {
    promptList.value = res.data || []
    if (promptList.value.length > 0 && !form.value.promptId) {
      form.value.promptId = String(promptList.value[0].promptId)
    }
  })
}

function showPromptPreview() {
  if (!form.value.promptId) {
    proxy.$modal.msgWarning('请先选择提示词')
    return
  }
  getPromptDetail(form.value.promptId).then(res => {
    promptPreviewContent.value = res.data.content
    previewVisible.value = true
  })
}

async function handleExtract() {
  if (!form.value.content.trim()) {
    proxy.$modal.msgWarning('请输入待抽取文本')
    return
  }
  if (!form.value.model) {
    proxy.$modal.msgWarning('请选择模型')
    return
  }
  if (!form.value.promptId) {
    proxy.$modal.msgWarning('请选择提示词')
    return
  }

  loading.value = true
  try {
    const response = await llmExtract(form.value)
    result.value = response.data
    proxy.$modal.msgSuccess('抽取成功')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.title {
  font-size: 32px;
  font-weight: bold;
  display: block;
  text-align: center;
  color: #333;
}

.card-header {
  justify-content: center;
}

.form-section {
  padding: 20px 40px;
}

.form-row {
  display: flex;
  align-items: center;
}

.label {
  width: 90px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.result-content {
  padding: 10px 0;
}
</style>
