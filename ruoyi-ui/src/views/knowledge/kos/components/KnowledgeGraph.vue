<template>
  <div ref="chartRef" style="width: 100%; height: 600px" />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  nodes: Array,
  links: Array
})

const chartRef = ref(null)
let chartInstance = null

function buildOption() {
  const categories = [...new Set(props.nodes.map(n => n.category))]
  const categoryColors = generateColors(categories.length)

  return {
    title: { text: '知识图谱', left: 'center', top: 10 },
    tooltip: {
      formatter: params => {
        if (params.dataType === 'node') {
          return `${params.data.name}<br/>类型: ${params.data.category}<br/>频次: ${params.data.value}`
        } else if (params.dataType === 'edge') {
          return `${params.data.source} -[${params.data.relation}]-> ${params.data.target}<br/>方法: ${params.data.method}<br/>得分: ${params.data.value.toFixed(2)}`
        }
      }
    },
    legend: [{ data: categories, top: 50, textStyle: { fontSize: 12 } }],
    series: [{
      type: 'graph',
      layout: 'force',
      scaleLimit: { min: 0.5, max: 8 },
      categories: categories.map(c => ({ name: c })),
      roam: true,
      label: {
        show: true,
        position: 'bottom',
        fontSize: 12,
        color: '#333',
        fontWeight: 500
      },
      emphasis: { focus: 'adjacency', lineStyle: { width: 3 } },
      force: { repulsion: 200, edgeLength: 100, gravity: 0.1 },
      data: props.nodes.map(n => ({
        id: n.id,
        name: n.name,
        category: n.category,
        value: n.value,
        symbolSize: n.symbolSize,
        itemStyle: { color: categoryColors[categories.indexOf(n.category) % categoryColors.length] }
      })),
      links: props.links.map(l => ({
        source: l.source,
        target: l.target,
        relation: l.relation,
        method: l.method,
        value: l.value,
        lineStyle: {
          color: l.method === 'RULE' ? '#5470c6' : '#91cc75',
          curveness: 0.3
        },
        label: {
          show: true,
          fontSize: 11,
          formatter: () => l.relation,
          color: '#666'
        }
      }))
    }]
  }
}

function generateColors(count) {
  const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
  return Array.from({ length: count }, (_, i) => colors[i % colors.length])
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value, 'macarons')
  chartInstance.setOption(buildOption())
}

function handleResize() {
  chartInstance?.resize()
}

watch(() => [props.nodes, props.links], () => {
  if (chartInstance) {
    chartInstance.dispose()
    initChart()
  }
}, { deep: true })

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>
