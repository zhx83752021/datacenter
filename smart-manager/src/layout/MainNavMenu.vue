<template>
  <el-menu
    :mode="mode"
    :default-active="activeMenu"
    router
    :ellipsis="mode === 'horizontal' ? false : undefined"
    class="main-nav-menu"
    :class="[`main-nav-menu--${mode}`]"
    @select="onSelect"
  >
    <!-- ========== 全院指标监控（含子菜单） ========== -->
    <el-sub-menu v-if="isAdmin || isPresident || isDirector" index="monitor-group">
      <template #title>
        <el-icon>
          <TrendCharts />
        </el-icon>
        <span>全院指标监控</span>
      </template>
      <el-menu-item index="/monitor">
        <el-icon>
          <TrendCharts />
        </el-icon>指标监测总览
      </el-menu-item>
      <el-menu-item index="/monitor/lib">
        <el-icon>
          <Collection />
        </el-icon>指标知识库
      </el-menu-item>
      <el-menu-item index="/monitor/graph">
        <el-icon>
          <Share />
        </el-icon>指标关联图谱
      </el-menu-item>
      <el-menu-item index="/monitor/target">
        <el-icon>
          <Aim />
        </el-icon>目标与预警
      </el-menu-item>
      <el-menu-item index="/monitor/feedback">
        <el-icon>
          <ChatLineSquare />
        </el-icon>数据反馈管理
      </el-menu-item>
      <el-menu-item index="/monitor/report">
        <el-icon>
          <Promotion />
        </el-icon>智能报告分发
      </el-menu-item>
    </el-sub-menu>

    <!-- ========== 运营决策门户（无子级） ========== -->
    <el-menu-item v-if="isAdmin || isPresident || isDirector" index="/portal">
      <template #title>
        <el-icon>
          <DataBoard />
        </el-icon>
        <span>运营决策门户</span>
      </template>
    </el-menu-item>

    <!-- ========== 决策驾驶舱（院长/科主任分流） ========== -->
    <el-menu-item v-if="isAdmin || isPresident" index="/cockpit">
      <template #title>
        <el-icon>
          <Aim />
        </el-icon>
        <span>决策驾驶舱</span>
      </template>
    </el-menu-item>
    <el-menu-item v-if="isDirector" index="/cockpit/dept">
      <template #title>
        <el-icon>
          <Aim />
        </el-icon>
        <span>科室驾驶舱</span>
      </template>
    </el-menu-item>

    <!-- ========== 数据分析报表（含子菜单） ========== -->
    <el-sub-menu index="report-group">
      <template #title>
        <el-icon>
          <Document />
        </el-icon>
        <span>数据分析报表</span>
      </template>
      <el-menu-item index="/reports">
        <el-icon>
          <Document />
        </el-icon>基础报表中心
      </el-menu-item>

      <el-menu-item index="/analysis/theme">
        <el-icon>
          <DataAnalysis />
        </el-icon>运营主题分析
      </el-menu-item>
    </el-sub-menu>

    <!-- ========== 医疗业务（含子菜单） ========== -->
    <el-sub-menu index="medical-group">
      <template #title>
        <el-icon>
          <UserFilled />
        </el-icon>
        <span>医疗业务</span>
      </template>
      <el-menu-item index="/medical/patient360">
        <el-icon>
          <UserFilled />
        </el-icon>患者360视图
      </el-menu-item>
      <el-menu-item index="/medical/ai-diagnosis">
        <el-icon>
          <MagicStick />
        </el-icon>智能辅助诊疗
      </el-menu-item>
    </el-sub-menu>

    <!-- ========== 看板管理（单页面） ========== -->
    <el-menu-item v-if="isAdmin || isPresident" index="/dashboard/list">
      <template #title>
        <el-icon>
          <Monitor />
        </el-icon>
        <span>看板管理</span>
      </template>
    </el-menu-item>

    <!-- ========== 系统管理（含子菜单，仅管理员） ========== -->
    <el-sub-menu v-if="isAdmin" index="system-group">
      <template #title>
        <el-icon>
          <Setting />
        </el-icon>
        <span>系统管理</span>
      </template>
      <el-menu-item index="/system/user">
        <el-icon>
          <User />
        </el-icon>用户管理
      </el-menu-item>
      <el-menu-item index="/system/role">
        <el-icon>
          <Stamp />
        </el-icon>角色管理
      </el-menu-item>
      <el-menu-item index="/system/menu">
        <el-icon>
          <Menu />
        </el-icon>菜单管理
      </el-menu-item>
      <el-menu-item index="/system/log">
        <el-icon>
          <Tickets />
        </el-icon>日志管理
      </el-menu-item>
      <el-menu-item index="/system/config">
        <el-icon>
          <Setting />
        </el-icon>参数配置
      </el-menu-item>
      <el-menu-item index="/system/dict">
        <el-icon>
          <Notebook />
        </el-icon>字典管理
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup lang="ts">
import {
  Monitor, TrendCharts, DataBoard, Aim, Document, UserFilled, Setting,
  Collection, Share, ChatLineSquare, Promotion, DataAnalysis, MagicStick,
  User, Stamp, Menu, Tickets, Notebook
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

defineProps<{
  mode: 'horizontal' | 'vertical'
}>()

const emit = defineEmits<{
  menuSelect: [index: string]
}>()

const route = useRoute()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)

const isAdmin = computed(() => userStore.roles.includes('admin') || userStore.roles.includes('ROLE_ADMIN'))
const isPresident = computed(() => userStore.roles.includes('president') || userStore.roles.includes('ROLE_PRESIDENT'))
const isDirector = computed(() => userStore.roles.includes('director') || userStore.roles.includes('ROLE_DIRECTOR'))

function onSelect(index: string) {
  emit('menuSelect', index)
}
</script>

<style scoped lang="scss">
.main-nav-menu--horizontal {
  border-bottom: none;
  height: 100%;
  background: transparent;

  :deep(.el-menu-item) {
    height: 100%;
    color: rgba(255, 255, 255, 0.75);
    font-size: 15px;
    font-weight: 500;
    border-bottom: 3px solid transparent;
    transition: all 0.3s ease;

    .el-icon {
      margin-right: 6px;
    }

    &:hover {
      background: transparent;
      color: #fff;
    }

    &.is-active {
      color: var(--primary-color) !important;
      border-bottom-color: var(--primary-color);
      background: linear-gradient(to top, rgba(13, 189, 168, 0.15), transparent);
      font-weight: 600;
    }
  }

  :deep(.el-sub-menu) {
    height: 100%;

    .el-sub-menu__title {
      height: 100% !important;
      line-height: 64px;
      color: rgba(255, 255, 255, 0.75);
      font-size: 15px;
      font-weight: 500;
      border-bottom: 3px solid transparent;
      transition: all 0.3s ease;

      .el-icon {
        margin-right: 6px;
        color: inherit;
      }

      .el-sub-menu__icon-arrow {
        color: rgba(255, 255, 255, 0.5);
      }

      &:hover {
        background: transparent !important;
        color: #fff;
      }
    }

    &.is-active {
      .el-sub-menu__title {
        color: var(--primary-color) !important;
        border-bottom-color: var(--primary-color);
        background: linear-gradient(to top, rgba(13, 189, 168, 0.15), transparent) !important;
        font-weight: 600;
      }
    }
  }
}

/* 抽屉内纵向菜单：浅色背景、触控友好高度；字号随屏宽收缩，避免长文案顶出横向滚动 */
.main-nav-menu--vertical {
  width: 100%;
  border-right: none;
  background: transparent;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    min-height: 48px;
    line-height: 1.35;
    align-items: center;
    font-size: clamp(13px, 3.6vw, 15px);
    padding-right: 8px !important;
    box-sizing: border-box;
  }

  :deep(.el-menu-item span),
  :deep(.el-sub-menu__title span) {
    overflow-wrap: anywhere;
    word-break: break-word;
    hyphens: auto;
  }

  :deep(.el-menu-item) {
    &.is-active {
      font-weight: 600;
    }
  }

  :deep(.el-sub-menu .el-menu-item) {
    min-height: 44px;
    padding-left: clamp(36px, 10vw, 48px) !important;
  }

  :deep(.el-icon) {
    flex-shrink: 0;
  }
}
</style>
