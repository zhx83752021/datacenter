<template>
    <div class="graph-container animate-enter">
        <div class="glass-panel main-panel">
            <!-- 头部控制栏 -->
            <div class="graph-header">
                <div class="left-info">
                    <div class="icon-wrapper">
                        <el-icon>
                            <Share />
                        </el-icon>
                    </div>
                    <div>
                        <h2 class="title">指标逻辑关联图谱</h2>
                        <p class="subtitle">展示各运营指标间的支撑、因果及数据依赖关系</p>
                    </div>
                </div>
                <div class="right-controls">
                    <el-select v-model="rootIndicator" placeholder="选择核心指标" class="glass-select" style="width: 200px">
                        <el-option label="全院总收入" value="income" />
                        <el-option label="门诊人次" value="outpatient" />
                        <el-option label="平均住院日" value="los" />
                    </el-select>
                    <el-button-group class="ml-4">
                        <el-button :icon="ZoomIn" @click="handleZoom('in')" />
                        <el-button :icon="ZoomOut" @click="handleZoom('out')" />
                        <el-button :icon="Refresh" @click="resetGraph" />
                    </el-button-group>
                    <el-button type="primary" class="ml-4" icon="FullScreen" round
                        @click="toggleFullscreen">全屏分析</el-button>
                </div>
            </div>

            <!-- 图谱主体 -->
            <div class="graph-viewport" ref="graphRef">
                <div class="graph-overlay" v-if="selectedNode">
                    <div class="node-detail-card glass-panel">
                        <div class="detail-h">
                            <span class="tag">{{ selectedNode.category }}</span>
                            <el-button link :icon="Close" @click="selectedNode = null" />
                        </div>
                        <h3 class="node-name">{{ selectedNode.name }}</h3>
                        <div class="node-stats">
                            <div class="stat">
                                <span class="lbl">当前值</span>
                                <span class="val">{{ selectedNode.value }}</span>
                            </div>
                            <div class="stat">
                                <span class="lbl">同比</span>
                                <span class="val" :class="selectedNode.trend">{{ selectedNode.yoy }}%</span>
                            </div>
                        </div>
                        <div class="node-desc">{{ selectedNode.desc }}</div>
                        <el-button type="primary" plain size="small" class="w-full mt-4"
                            @click="goToDetail">查看深度分析</el-button>
                    </div>
                </div>

                <!-- 图例 -->
                <div class="graph-legend">
                    <div class="legend-item"><span class="dot income"></span> 经济运行</div>
                    <div class="legend-item"><span class="dot quality"></span> 医疗质量</div>
                    <div class="legend-item"><span class="dot efficiency"></span> 产出效率</div>
                    <div class="legend-item"><span class="dot human"></span> 人力资源</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { Share, ZoomIn, ZoomOut, Refresh, Close } from '@element-plus/icons-vue'
import { getIndicatorGraph } from '../../api/monitor'

const router = useRouter()
const graphRef = ref<HTMLElement | null>(null)
let myChart: echarts.ECharts | null = null
const rootIndicator = ref('income')
const selectedNode = ref<any>(null)
const loading = ref(false)

const getColorByCategory = (categoryId: any) => {
    const map: any = {
        '1': '#0dbda8', // 经济运行
        '2': '#f59e0b', // 规模产出
        '3': '#ef4444', // 医疗质量
        '4': '#8b5cf6', // 持续发展
    }
    return map[String(categoryId)] || '#64748b'
}

const getCategoryName = (categoryId: any) => {
    const map: any = {
        '1': '经济运行',
        '2': '产出效率',
        '3': '医疗质量',
        '4': '持续发展',
    }
    return map[String(categoryId)] || '其他'
}

const loadData = async () => {
    loading.value = true
    try {
        const res: any = await getIndicatorGraph()
        // request.ts 拦截器已解包，res 直接就是 result.data
        const data = res
        if (data && data.nodes) {
            const nodes = data.nodes.map((n: any) => ({
                id: n.id,
                name: n.name,
                category: getCategoryName(n.category),
                symbolSize: n.id === rootIndicator.value ? 80 : 50,
                itemStyle: { color: getColorByCategory(n.category) },
                value: '查看详情',
                desc: n.name + '系统动态计算指标'
            }))

            const links = data.links.map((l: any) => ({
                source: l.source,
                target: l.target,
                label: { show: true, formatter: l.label || '支撑' }
            }))

            renderGraph(nodes, links)
        }
    } finally {
        loading.value = false
    }
}

const renderGraph = (nodes: any[], links: any[]) => {
    if (!graphRef.value) return
    if (!myChart) {
        myChart = echarts.init(graphRef.value)
    }

    const option: any = {
        tooltip: { trigger: 'item', formatter: '{b}' },
        series: [{
            type: 'graph',
            layout: 'force',
            data: nodes,
            links: links,
            roam: true,
            draggable: true,
            // @ts-ignore
            focusNodeAdjacency: true,
            force: {
                repulsion: 2000,
                edgeLength: [180, 280],
                gravity: 0.1
            },
            label: {
                show: true,
                position: 'inside',
                fontSize: 11,
                color: '#fff',
                fontWeight: 'bold'
            },
            lineStyle: {
                color: '#cbd5e1',
                width: 2,
                curveness: 0.2,
                opacity: 0.6
            },
            emphasis: {
                lineStyle: { width: 4, opacity: 1 }
            },
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 8]
        }]
    }

    if (myChart) {
        myChart.setOption(option)

        myChart.on('click', (params: any) => {
            if (params.dataType === 'node') {
                selectedNode.value = params.data
            }
        })
    }
}

const handleZoom = (type: 'in' | 'out') => {
    if (!myChart) return
    const option = myChart.getOption() as any
    const zoom = option.series[0].zoom || 1
    myChart.setOption({
        series: [{ zoom: type === 'in' ? zoom * 1.2 : zoom / 1.2 }]
    })
}

const resetGraph = () => {
    myChart?.setOption({ series: [{ zoom: 1, center: null }] })
}

const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
        graphRef.value?.requestFullscreen()
    } else {
        document.exitFullscreen()
    }
}

const goToDetail = () => {
    router.push(`/monitor/analysis/${selectedNode.value.id}`)
}

onMounted(() => {
    loadData()
    window.addEventListener('resize', () => myChart?.resize())
})

onUnmounted(() => {
    window.removeEventListener('resize', () => myChart?.resize())
    myChart?.dispose()
})
</script>

<style scoped lang="scss">
.graph-container {
    min-height: calc(100vh - 160px);
    padding-bottom: 20px;

    .main-panel {
        height: calc(100vh - 180px);
        padding: 0;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        border: 1px solid #e2e8f0;
        background: #fff;
    }
}

.graph-header {
    padding: 24px;
    border-bottom: 1px solid #f1f5f9;
    background: #fafafa;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left-info {
        display: flex;
        align-items: center;
        gap: 16px;

        .icon-wrapper {
            width: 44px;
            height: 44px;
            background: linear-gradient(135deg, #0dbda8, #2dd4bf);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 22px;
            box-shadow: 0 4px 12px rgba(13, 189, 168, 0.3);
        }

        .title {
            margin: 0;
            font-size: 18px;
            color: #1e293b;
        }

        .subtitle {
            margin: 4px 0 0 0;
            font-size: 13px;
            color: #64748b;
        }
    }

    .right-controls {
        display: flex;
        align-items: center;

        .glass-select {
            :deep(.el-input__wrapper) {
                background: #f1f5f9;
                box-shadow: none !important;
                border-radius: 10px;
            }
        }
    }
}

.graph-viewport {
    flex: 1;
    position: relative;
    background: radial-gradient(circle at center, #ffffff 0%, #f8fafc 100%);
}

.graph-overlay {
    position: absolute;
    top: 24px;
    right: 24px;
    z-index: 10;
    width: 320px;
}

.node-detail-card {
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid #0dbda8;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);

    .detail-h {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .tag {
            background: #f0fdfa;
            color: #0dbda8;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
        }
    }

    .node-name {
        font-size: 18px;
        margin: 0 0 16px 0;
        color: #1e293b;
    }

    .node-stats {
        display: flex;
        gap: 20px;
        margin-bottom: 16px;

        .stat {
            .lbl {
                display: block;
                font-size: 12px;
                color: #64748b;
                margin-bottom: 4px;
            }

            .val {
                font-size: 16px;
                font-weight: 700;
                color: #1e293b;
            }

            .up {
                color: #10b981;
            }

            .down {
                color: #ef4444;
            }
        }
    }

    .node-desc {
        font-size: 13px;
        color: #475569;
        line-height: 1.6;
    }
}

.graph-legend {
    position: absolute;
    bottom: 24px;
    left: 24px;
    background: rgba(255, 255, 255, 0.8);
    padding: 12px 20px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;

    .legend-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 12px;
        margin-bottom: 6px;
        color: #475569;

        &:last-child {
            margin-bottom: 0;
        }

        .dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;

            &.income {
                background: #0dbda8;
            }

            &.efficiency {
                background: #f59e0b;
            }

            &.quality {
                background: #ef4444;
            }

            &.human {
                background: #8b5cf6;
            }
        }
    }
}

.w-full {
    width: 100%;
}

.mt-4 {
    margin-top: 16px;
}

.ml-4 {
    margin-left: 16px;
}

.animate-enter {
    animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
