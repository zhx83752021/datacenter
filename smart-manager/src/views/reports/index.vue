<template>
  <div class="reports-container">
    <div class="header-section animate-enter">
      <div class="title-group">
        <div class="icon-box">
          <el-icon :size="24" color="#fff">
            <Document />
          </el-icon>
        </div>
        <div class="text-col">
          <span class="main">专项报表中心</span>
        </div>
      </div>
      <div class="actions">
        <el-button type="primary" :icon="Plus" class="new-btn" round>新建报表</el-button>
      </div>
    </div>

    <div class="filter-glass animate-enter" style="animation-delay: 0.1s">
      <div class="left-filters">
        <el-select v-model="form.type" placeholder="报表类型" class="glass-select" clearable style="width: 160px">
          <el-option label="月度报表" value="monthly" />
          <el-option label="季度分析" value="quarterly" />
          <el-option label="年度总结" value="yearly" />
        </el-select>
        <el-select v-model="form.dept" placeholder="所属科室" class="glass-select" clearable style="width: 160px">
          <el-option label="智慧管理中心" value="center" />
          <el-option label="信息科" value="xinxi" />
          <el-option label="医务处" value="yiwu" />
        </el-select>
        <el-input v-model="form.keyword" placeholder="搜索报表名称..." :prefix-icon="Search" class="glass-input"
          style="width: 200px" />
      </div>
      <div class="view-switch">
        <div class="switch-item" :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'"><el-icon>
            <Grid />
          </el-icon></div>
        <div class="switch-item" :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'"><el-icon>
            <Menu />
          </el-icon></div>
      </div>
    </div>

    <!-- Grid View -->
    <div class="report-grid mt-4" v-if="viewMode === 'grid'">
      <div class="report-card animate-enter" v-for="(item, index) in tableData" :key="index"
        :style="{ animationDelay: (index * 0.05 + 0.2) + 's' }" @click="handleExport(item)">
        <div class="card-cover" :class="getTypeClass(item.type)">
          <div class="type-badge">{{ item.type }}</div>
          <div class="cover-icon">
            <el-icon :size="48">
              <DataAnalysis />
            </el-icon>
          </div>
          <div class="hover-overlay">
            <el-button type="primary" circle :icon="View" @click.stop="handlePreview(item)"></el-button>
            <el-button type="success" circle :icon="Download" @click.stop="handleExport(item)"></el-button>
          </div>
        </div>
        <div class="card-info">
          <div class="meta-row">
            <span class="dept-tag">{{ item.dept }}</span>
            <span class="date">{{ item.date }}</span>
          </div>
          <div class="name" :title="item.name">{{ item.name }}</div>
          <div class="status-row">
            <div class="status-badge" :class="item.status === '已发布' ? 'published' : 'draft'">
              <span class="dot"></span> {{ item.status }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- List View -->
    <div class="report-list glass-panel mt-4 animate-enter" style="animation-delay: 0.2s" v-else>
      <div class="table-responsive">
      <el-table :data="filteredData" style="width: 100%" class="premium-table">
        <el-table-column prop="name" label="报表名称" min-width="240">
          <template #default="{ row }">
            <div class="name-cell">
              <div class="icon-file" :class="getTypeClass(row.type)"><el-icon>
                  <Document />
                </el-icon></div>
              <span class="name-text">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="dept" label="科室" width="120">
          <template #default="{ row }"><el-tag effect="plain" type="info" size="small">{{ row.dept
          }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="date" label="生成时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span class="status-text" :class="row.status === '已发布' ? 'text-success' : 'text-warning'">{{ row.status
            }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" @click="handleExport(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted, watch } from 'vue'
import { Document, Plus, Search, Grid, Menu, DataAnalysis, View, Download } from '@element-plus/icons-vue'
import { getReportList } from '@/api/report'
import { ElMessage } from 'element-plus'

const viewMode = ref('grid')
const form = reactive({ type: '', dept: '', keyword: '' })
const loading = ref(false)
const tableData = ref<any[]>([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await getReportList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      name: form.keyword || undefined,
      type: form.type === 'monthly' ? '月报' : form.type === 'quarterly' ? '季报' : form.type === 'yearly' ? '年报' : undefined,
      dept: form.dept === 'xinxi' ? '信息科' : form.dept === 'yiwu' ? '医务处' : form.dept === 'center' ? '智慧管理中心' : undefined
    })
    if (res && res.records) {
      tableData.value = res.records
      pagination.total = res.total
    } else {
      tableData.value = Array.isArray(res) ? res : []
    }
  } catch (e) {
    console.error('加载报表失败:', e)
  } finally {
    loading.value = false
  }
}

const filteredData = computed(() => tableData.value)

onMounted(() => {
  fetchList()
})

watch([() => form.type, () => form.dept, () => form.keyword], () => {
  pagination.pageNum = 1
  fetchList()
})

const getTypeClass = (type: string) => {
  if (!type) return 'bg-teal'
  if (type.includes('月')) return 'bg-teal'
  if (type.includes('季')) return 'bg-blue'
  if (type.includes('年')) return 'bg-purple'
  return 'bg-orange'
}

const handleExport = async (row: any) => {
  ElMessage.success(`开始提取: ${row.name}`);
  try {
    const token = localStorage.getItem('token');
    const response = await fetch(`/api/sm/report/export/${row.id}`, {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Export API failed');
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${row.name}.xlsx`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  } catch(e) {
    ElMessage.error(`下载失败: ${e}`);
  }
}

const handlePreview = (row: any) => {
  ElMessage.info(`预览报表: ${row.name}`)
}
</script>

<style scoped lang="scss">
.reports-container {
  padding-bottom: 40px;

  .header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .title-group {
      display: flex;
      align-items: center;
      gap: 16px;

      .icon-box {
        width: 48px;
        height: 48px;
        background: linear-gradient(135deg, #0dbda8, #4FC3F7);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(13, 189, 168, 0.3);
      }

      .text-col {
        display: flex;
        flex-direction: column;

        .main {
          font-size: 20px;
          font-weight: 700;
          color: var(--text-primary);
        }

        .sub {
          font-size: 12px;
          color: var(--text-secondary);
          opacity: 0.8;
        }
      }
    }

    .new-btn {
      background: #0dbda8;
      border: none;
      padding: 10px 24px;
      font-weight: 600;
      box-shadow: 0 4px 12px rgba(13, 189, 168, 0.2);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(13, 189, 168, 0.3);
      }
    }
  }

  .filter-glass {
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(20px);
    padding: 16px 24px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left-filters {
      display: flex;
      gap: 12px;
      flex: 1;
    }

    .view-switch {
      display: flex;
      background: #f1f5f9;
      padding: 4px;
      border-radius: 8px;
      gap: 4px;

      .switch-item {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 6px;
        cursor: pointer;
        color: var(--text-secondary);
        transition: all 0.2s;

        &.active {
          background: #fff;
          color: var(--primary-color);
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
      }
    }
  }

  // Grid View
  .report-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 24px;

    .report-card {
      background: #fff;
      border-radius: 16px;
      border: 1px solid #e2e8f0;
      overflow: hidden;
      transition: all 0.3s;
      cursor: pointer;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.05);
        border-color: var(--primary-color);

        .cover-icon {
          transform: scale(1.1);
          opacity: 0.1;
        }

        .hover-overlay {
          opacity: 1;
          transform: translateY(0);
          pointer-events: auto;
        }
      }

      .card-cover {
        height: 140px;
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;

        &.bg-teal {
          background: linear-gradient(135deg, rgba(13, 189, 168, 0.1), rgba(13, 189, 168, 0.05));
          color: #0dbda8;
        }

        &.bg-blue {
          background: linear-gradient(135deg, rgba(79, 195, 247, 0.1), rgba(79, 195, 247, 0.05));
          color: #4FC3F7;
        }

        &.bg-purple {
          background: linear-gradient(135deg, rgba(156, 39, 176, 0.1), rgba(156, 39, 176, 0.05));
          color: #9c27b0;
        }

        &.bg-orange {
          background: linear-gradient(135deg, rgba(255, 183, 77, 0.1), rgba(255, 183, 77, 0.05));
          color: #FFB74D;
        }

        .type-badge {
          position: absolute;
          top: 12px;
          left: 12px;
          padding: 4px 10px;
          background: #fff;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 600;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          opacity: 0.9;
        }

        .cover-icon {
          transition: all 0.4s;
          opacity: 0.8;
        }

        .hover-overlay {
          position: absolute;
          width: 100%;
          height: 100%;
          top: 0;
          left: 0;
          background: rgba(255, 255, 255, 0.4);
          backdrop-filter: blur(4px);
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 12px;
          opacity: 0;
          transform: translateY(10px);
          transition: all 0.3s;
          pointer-events: none;
        }
      }

      .card-info {
        padding: 16px;

        .meta-row {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }

        .name {
          font-size: 15px;
          font-weight: 700;
          color: var(--text-primary);
          margin-bottom: 12px;
          line-height: 1.4;
          height: 42px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .status-row {
          .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            font-size: 12px;
            padding: 2px 8px;
            border-radius: 4px;
            background: #f8fafc;
            color: var(--text-secondary);

            .dot {
              width: 6px;
              height: 6px;
              border-radius: 50%;
              background: #ccc;
            }

            &.published {
              background: #ecfdf5;
              color: var(--success);

              .dot {
                background: var(--success);
              }
            }

            &.draft {
              background: #fff7ed;
              color: #f97316;

              .dot {
                background: #f97316;
              }
            }
          }
        }
      }
    }
  }

  // List View
  .glass-panel {
    background: #fff;
    border-radius: 20px;
    border: 1px solid #e2e8f0;
    padding: 24px;
  }

  .name-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    font-weight: 600;

    .icon-file {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;

      &.bg-teal {
        background: rgba(13, 189, 168, 0.1);
        color: #0dbda8;
      }

      &.bg-blue {
        background: rgba(79, 195, 247, 0.1);
        color: #4FC3F7;
      }
    }
  }

  .text-success {
    color: var(--success);
  }

  .text-warning {
    color: #f97316;
  }

  // Global
  .mt-4 {
    margin-top: 24px;
  }

  .glass-select,
  .glass-input {
    :deep(.el-input__wrapper) {
      box-shadow: none !important;
      background: transparent;
      border: 1px solid transparent;
      border-bottom: 1px solid #e2e8f0;
      border-radius: 0;
      padding-left: 0;
      transition: all 0.3s;

      &:hover,
      &.is-focus {
        border-bottom-color: var(--primary-color);
      }
    }
  }

  .animate-enter {
    animation: fadeInUp 0.6s ease-out forwards;
    opacity: 0;
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

  @media (max-width: 768px) {
    .header-section {
      flex-direction: column;
      align-items: stretch;
      gap: 16px;

      .title-group .text-col .main {
        font-size: clamp(17px, 4.5vw, 20px);
      }

      .actions {
        align-self: stretch;

        .new-btn {
          width: 100%;
        }
      }
    }

    .filter-glass {
      flex-direction: column;
      align-items: stretch;
      gap: 16px;
      padding: 16px;

      .left-filters {
        flex-direction: column;
        width: 100%;

        .glass-select,
        .glass-input {
          width: 100% !important;
        }
      }

      .view-switch {
        align-self: center;
      }
    }

    .report-grid {
      grid-template-columns: minmax(0, 1fr);
      gap: 16px;
    }

    .glass-panel {
      padding: 16px;
    }
  }
}
</style>
