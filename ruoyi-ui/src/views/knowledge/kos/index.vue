<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">文本大数据挖掘与知识计算</span>
        </div>
      </template>

      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="文本内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入待分析的文本..."
            maxlength="20000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fillExample">示例文本</el-button>
          <el-button @click="clearForm">清空</el-button>
        </el-form-item>

        <el-collapse>
          <el-collapse-item title="参数设置" name="params">
            <el-form-item label="最大实体数">
              <el-input-number v-model="form.maxEntities" :min="1" :max="500" />
            </el-form-item>
            <el-form-item label="最小词频">
              <el-input-number v-model="form.minFrequency" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="共现窗口">
              <el-input-number v-model="form.cooccurrenceWindow" :min="10" :max="1000" :step="10" />
            </el-form-item>
            <el-form-item label="最小共现次数">
              <el-input-number v-model="form.minCooccurrence" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="抽取策略">
              <el-checkbox v-model="form.enableRuleRelation">规则关系</el-checkbox>
              <el-checkbox v-model="form.enableCooccurrence">共现关系</el-checkbox>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleExtract" size="large">
            开始解析
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="result" shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>解析结果</span>
          <el-tag type="success" size="small">实体数: {{ result.entityCount }}</el-tag>
          <el-tag type="info" size="small" style="margin-left: 10px">关系数: {{ result.relationCount }}</el-tag>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="知识图谱" name="graph">
          <knowledge-graph v-if="result" :nodes="result.nodes" :links="result.links" />
        </el-tab-pane>
        <el-tab-pane label="原始结果" name="raw">
          <el-table :data="result.entities" border>
            <el-table-column prop="normalizedName" label="实体" width="200" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="frequency" label="频次" width="80" />
            <el-table-column prop="score" label="得分" width="80" />
          </el-table>
          <el-table :data="result.relations" border style="margin-top: 20px">
            <el-table-column prop="sourceName" label="源实体" width="150" />
            <el-table-column prop="relationType" label="关系" width="120" />
            <el-table-column prop="targetName" label="目标实体" width="150" />
            <el-table-column prop="method" label="抽取方法" width="100" />
            <el-table-column prop="score" label="得分" width="80" />
            <el-table-column prop="evidence" label="证据" show-overflow-tooltip />
          </el-table>
          <el-collapse style="margin-top: 20px">
            <el-collapse-item title="JSON 数据">
              <pre>{{ JSON.stringify(result, null, 2) }}</pre>
            </el-collapse-item>
          </el-collapse>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-button v-hasPermi="['knowledge:kos:query']" type="primary" plain @click="showRuleDrawer = true" style="position: fixed; right: 30px; bottom: 50px">
      KOS规则库
    </el-button>

    <kos-rule-drawer v-model:visible="showRuleDrawer" />
  </div>
</template>

<script setup name="KosExtraction">
import { ref } from 'vue'
import { extract as extractApi } from "@/api/knowledge/kos"
import KnowledgeGraph from "./components/KnowledgeGraph.vue"
import KosRuleDrawer from "./components/KosRuleDrawer.vue"

const form = ref({
  content: '',
  maxEntities: 200,
  minFrequency: 1,
  cooccurrenceWindow: 120,
  minCooccurrence: 1,
  enableRuleRelation: true,
  enableCooccurrence: true
})

const loading = ref(false)
const result = ref(null)
const activeTab = ref('graph')
const showRuleDrawer = ref(false)

const EXAMPLE_TEXT = `水稻是我国重要的粮食作物，具有悠久的种植历史。长江流域是水稻的主要产区，江苏、浙江等省份种植面积广阔。杂交水稻技术的推广大大提高了产量，袁隆平院士为此做出了巨大贡献。玉米是另一种重要的粮食作物，广泛种植于东北平原。小麦主要分布在华北平原。`;

function fillExample() {
  form.value.content = EXAMPLE_TEXT;
}

function clearForm() {
  form.value.content = '';
  result.value = null;
}

async function handleExtract() {
  if (!form.value.content.trim()) {
    proxy.$modal.msgWarning('请输入文本内容');
    return;
  }

  loading.value = true;
  try {
    const response = await extractApi(form.value);
    result.value = response.data;
    proxy.$modal.msgSuccess('解析成功');
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
}
.title {
  font-size: 18px;
  font-weight: bold;
}
</style>
