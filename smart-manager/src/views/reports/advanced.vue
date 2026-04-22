<template>
    <div class="bi-designer-container animate-enter">
        <!-- Header Toolbar -->
        <div class="designer-header">
            <div class="left">
                <div class="back-btn"><el-icon>
                        <ArrowLeft />
                    </el-icon></div>
                <div class="file-info">
                    <span class="file-name">未命名报表_20240520</span>
                    <el-tag size="small" type="warning" effect="dark" class="status-tag">Draft</el-tag>
                </div>
            </div>
            <div class="center-tools">
                <el-button-group>
                    <el-button icon="Mouse" plain class="tool-btn active" />
                    <el-button icon="Crop" plain class="tool-btn" />
                    <el-button icon="Search" plain class="tool-btn" />
                </el-button-group>
            </div>
            <div class="right-actions">
                <el-button icon="View" plain>预览</el-button>
                <el-button type="primary" icon="Download">保存 & 发布</el-button>
            </div>
        </div>

        <div class="designer-body">
            <!-- Left: Data & Assets -->
            <div class="sidebar-left glass-panel">
                <el-tabs v-model="activeSide" class="side-tabs" stretch>
                    <el-tab-pane name="data">
                        <template #label><el-icon>
                                <DataLine />
                            </el-icon></template>
                        <div class="panel-content">
                            <div class="search-row">
                                <el-input v-model="searchField" placeholder="搜索字段..." prefix-icon="Search"
                                    class="mini-input" />
                            </div>
                            <div class="dataset-tree custom-scrollbar">
                                <el-collapse v-model="activeDatasets">
                                    <el-collapse-item title="患者主索引 (MPI)" name="1">
                                        <div class="field-item draggable" draggable="true">
                                            <el-icon><collection-tag /></el-icon> 患者ID
                                        </div>
                                        <div class="field-item draggable" draggable="true">
                                            <el-icon>
                                                <user />
                                            </el-icon> 姓名
                                        </div>
                                        <div class="field-item draggable" draggable="true">
                                            <el-icon>
                                                <female />
                                            </el-icon> 性别
                                        </div>
                                    </el-collapse-item>
                                    <el-collapse-item title="住院医嘱明细" name="2">
                                        <div class="field-item draggable" draggable="true">
                                            <el-icon>
                                                <tickets />
                                            </el-icon> 医嘱内容的
                                        </div>
                                        <div class="field-item draggable" draggable="true">
                                            <el-icon>
                                                <money />
                                            </el-icon> 金额
                                        </div>
                                    </el-collapse-item>
                                </el-collapse>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane name="charts">
                        <template #label><el-icon>
                                <PieChart />
                            </el-icon></template>
                        <div class="panel-content grid-layout">
                            <div class="chart-thumb">
                                <el-icon size="24">
                                    <Histogram />
                                </el-icon>
                                <span>柱状图</span>
                            </div>
                            <div class="chart-thumb">
                                <el-icon size="24">
                                    <TrendCharts />
                                </el-icon>
                                <span>折线图</span>
                            </div>
                            <div class="chart-thumb">
                                <el-icon size="24">
                                    <PieChart />
                                </el-icon>
                                <span>饼图</span>
                            </div>
                            <div class="chart-thumb">
                                <el-icon size="24">
                                    <DataBoard />
                                </el-icon>
                                <span>透视表</span>
                            </div>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>

            <!-- Center: Canvas -->
            <div class="canvas-area">
                <div class="canvas-toolbar">
                    <span class="page-size">A4 纵向</span>
                    <div class="zoom-ctrl">
                        <el-icon>
                            <Minus />
                        </el-icon>
                        <span>100%</span>
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </div>
                </div>
                <div class="canvas-stage custom-scrollbar">
                    <div class="paper-sheet">
                        <!-- Drop Zone / Grid -->
                        <div class="grid-bg"></div>

                        <!-- Mock Chart Component on Canvas -->
                        <div class="chart-container selected"
                            style="top: 40px; left: 40px; width: 500px; height: 300px;">
                            <div class="c-header">全院月度收入趋势</div>
                            <div class="c-body" ref="demoChartRef"></div>
                            <div class="resize-handle"></div>
                            <div class="selection-border"></div>
                        </div>

                        <!-- Empty State -->
                        <div class="empty-placeholder" style="top: 400px; left: 40px; width: 500px; height: 200px;">
                            <el-icon size="32" color="#cbd5e1">
                                <Plus />
                            </el-icon>
                            <span>拖拽组件至此</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right: Properties -->
            <div class="sidebar-right glass-panel">
                <div class="prop-header">属性配置</div>
                <div class="prop-content custom-scrollbar">
                    <el-form label-position="top" size="small">
                        <el-form-item label="图表标题">
                            <el-input value="全院月度收入趋势" />
                        </el-form-item>
                        <el-divider content-position="left">数据绑定</el-divider>
                        <el-form-item label="X轴 (维度)">
                            <div class="field-drop"><el-icon>
                                    <Timer />
                                </el-icon> 结算日期</div>
                        </el-form-item>
                        <el-form-item label="Y轴 (度量)">
                            <div class="field-drop"><el-icon>
                                    <Money />
                                </el-icon> 总费用</div>
                        </el-form-item>
                        <el-divider content-position="left">样式设置</el-divider>
                        <el-form-item label="配色方案">
                            <div class="color-picker-mock">
                                <span class="bubble" style="background:#0dbda8"></span>
                                <span class="bubble" style="background:#3b82f6"></span>
                                <span class="bubble" style="background:#f59e0b"></span>
                            </div>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import {
    ArrowLeft, Mouse, Crop, Search, View, Download,
    DataLine, PieChart, Histogram, TrendCharts, DataBoard,
    CollectionTag, User, Female, Tickets, Money, Minus, Plus, Timer
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const activeSide = ref('data')
const activeDatasets = ref(['1'])
const searchField = ref('')
const demoChartRef = ref<HTMLElement | null>(null)

onMounted(() => {
    nextTick(() => {
        if (demoChartRef.value) {
            const chart = echarts.init(demoChartRef.value)
            chart.setOption({
                grid: { top: 20, right: 20, bottom: 30, left: 40 },
                xAxis: { type: 'category', data: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'] },
                yAxis: { type: 'value' },
                series: [{
                    type: 'line',
                    data: [820, 932, 901, 934, 1290, 1330],
                    smooth: true,
                    itemStyle: { color: '#0dbda8' },
                    areaStyle: { opacity: 0.1 }
                }]
            })
            window.addEventListener('resize', () => chart.resize())
        }
    })
})
</script>

<style scoped lang="scss">
.bi-designer-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: #f1f5f9;
}

.designer-header {
    height: 56px;
    background: #fff;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 16px;
    flex-shrink: 0;

    .left {
        display: flex;
        align-items: center;
        gap: 16px;

        .back-btn {
            cursor: pointer;
            color: #64748b;

            &:hover {
                color: #1e293b;
            }
        }

        .file-info {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            color: #1e293b;
            font-size: 14px;
        }
    }
}

.designer-body {
    flex: 1;
    display: flex;
    overflow: hidden;
}

.sidebar-left {
    width: 250px;
    background: #fff;
    border-right: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;

    .panel-content {
        padding: 12px;
        height: 100%;
        display: flex;
        flex-direction: column;
    }

    .search-row {
        margin-bottom: 12px;
    }

    .dataset-tree {
        flex: 1;
        overflow-y: auto;

        .field-item {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px;
            margin-bottom: 2px;
            border-radius: 4px;
            font-size: 13px;
            color: #475569;
            cursor: grab;

            &:hover {
                background: #f8fafc;
                color: #0dbda8;
            }

            &:active {
                cursor: grabbing;
            }
        }
    }

    .grid-layout {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 12px;

        .chart-thumb {
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 16px;
            cursor: pointer;

            &:hover {
                border-color: #0dbda8;
                background: #f0fdfa;
                color: #0dbda8;
            }

            span {
                font-size: 12px;
            }
        }
    }
}

.sidebar-right {
    width: 280px;
    background: #fff;
    border-left: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;

    .prop-header {
        height: 48px;
        border-bottom: 1px solid #f1f5f9;
        display: flex;
        align-items: center;
        padding: 0 16px;
        font-weight: 600;
        font-size: 14px;
    }

    .prop-content {
        flex: 1;
        padding: 16px;
        overflow-y: auto;

        .field-drop {
            background: #f1f5f9;
            border: 1px dashed #cbd5e1;
            border-radius: 6px;
            padding: 6px 10px;
            font-size: 12px;
            color: #475569;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .color-picker-mock {
            display: flex;
            gap: 8px;

            .bubble {
                width: 24px;
                height: 24px;
                border-radius: 50%;
                border: 1px solid rgba(0, 0, 0, 0.1);
                cursor: pointer;
            }
        }
    }
}

.canvas-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
    background: #f8fafc;

    .canvas-toolbar {
        height: 40px;
        background: #fff;
        border-bottom: 1px solid #e2e8f0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 20px;
        font-size: 12px;
        color: #64748b;

        .zoom-ctrl {
            display: flex;
            align-items: center;
            gap: 10px;
            cursor: pointer;
        }
    }

    .canvas-stage {
        flex: 1;
        padding: 40px;
        overflow: auto;
        display: flex;
        justify-content: center;

        .paper-sheet {
            width: 794px;
            height: 1123px; // A4 size px at 96dpi approx
            background: #fff;
            box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.1);
            position: relative;
        }

        .grid-bg {
            position: absolute;
            inset: 0;
            background-image: radial-gradient(#e2e8f0 1px, transparent 1px);
            background-size: 20px 20px;
            opacity: 0.5;
            pointer-events: none;
        }

        .chart-container {
            position: absolute;
            border: 1px solid transparent;
            display: flex;
            flex-direction: column;
            background: #fff;

            &.selected {
                outline: 1px solid #0dbda8;

                .selection-border {
                    position: absolute;
                    inset: -2px;
                    border: 2px solid #0dbda8;
                    pointer-events: none;
                }

                .resize-handle {
                    position: absolute;
                    bottom: -4px;
                    right: -4px;
                    width: 8px;
                    height: 8px;
                    background: #0dbda8;
                    cursor: se-resize;
                }
            }

            .c-header {
                padding: 8px 12px;
                font-weight: 600;
                font-size: 14px;
            }

            .c-body {
                flex: 1;
                width: 100%;
            }
        }

        .empty-placeholder {
            position: absolute;
            border: 2px dashed #e2e8f0;
            border-radius: 8px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 10px;
            color: #94a3b8;
            font-size: 13px;
        }
    }
}

.animate-enter {
    animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}
</style>
