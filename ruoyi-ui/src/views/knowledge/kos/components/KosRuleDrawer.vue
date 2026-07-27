<template>
  <el-drawer v-model="visible" title="KOS 规则库" size="50%">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="词条管理" name="terms">
        <el-button type="primary" icon="Plus" @click="handleAddTerm" style="margin-bottom: 10px">新增</el-button>
        <el-table :data="terms" border>
          <el-table-column prop="name" label="名称" width="150" />
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column prop="priority" label="优先级" width="80" />
          <el-table-column label="别名">
            <template #default="scope">
              {{ scope.row.aliases.join(', ') }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="关系规则" name="rules">
        <el-button type="primary" icon="Plus" @click="handleAddRule" style="margin-bottom: 10px">新增</el-button>
        <el-table :data="rules" border>
          <el-table-column prop="name" label="规则名称" width="150" />
          <el-table-column prop="relationType" label="关系类型" width="120" />
          <el-table-column prop="triggerPattern" label="触发模式" show-overflow-tooltip />
          <el-table-column prop="direction" label="方向" width="80" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue'
import { listTerms, listRules } from '@/api/knowledge/kos'

const visible = defineModel('visible')
const activeTab = ref('terms')
const terms = ref([])
const rules = ref([])

async function loadTerms() {
  const res = await listTerms({})
  terms.value = res.data || []
}

async function loadRules() {
  const res = await listRules({})
  rules.value = res.data || []
}

function handleAddTerm() {
  visible.value = false
}

function handleAddRule() {
  visible.value = false
}

loadTerms()
loadRules()
</script>
