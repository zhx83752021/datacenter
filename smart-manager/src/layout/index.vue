<template>
  <el-container class="layout-container is-frontend" :class="{ 'is-embedded': isEmbedded }">
    <el-header v-if="!isEmbedded" class="top-header">
      <div class="header-inner">
        <div class="logo">
          <el-icon :size="28" color="#0dbda8">
            <Monitor />
          </el-icon>
          <span class="title">智慧管理平台</span>
        </div>

        <div class="nav-menu">
          <el-menu :default-active="activeMenu" mode="horizontal" router class="el-menu-horizontal" :ellipsis="false">
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
        </div>

        <div class="header-right">
          <!-- 主题切换按钮 -->
          <el-tooltip :content="isDark ? '切换到亮色模式' : '切换到暗黑模式'" placement="bottom">
            <el-button circle class="theme-toggle-btn" @click="toggleTheme">
              <el-icon :size="18">
                <Sunny v-if="isDark" />
                <Moon v-else />
              </el-icon>
            </el-button>
          </el-tooltip>

          <div class="user-profile">
            <el-avatar :size="32"
              :src="userStore.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
            <el-dropdown>
              <span class="el-dropdown-link">
                {{ userStore.name || 'Admin' }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/system/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <div class="content-wrapper" :class="{ 'is-embedded': isEmbedded }">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </el-main>

    <el-footer v-if="!isEmbedded" class="footer">
      <div class="copyright">© 2024 智慧医疗管理平台 Smart Management Platform. All rights reserved.</div>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import {
  Monitor, TrendCharts, DataBoard, Aim, Document, Sunny, Moon, UserFilled, Setting,
  Collection, Share, ChatLineSquare, Promotion, DataAnalysis, MagicStick,
  User, Stamp, Menu, Tickets, Notebook, ArrowDown
} from '@element-plus/icons-vue'
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from '../composables/useTheme'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)
const isEmbedded = computed(() => route.query.embedded === 'true')

// 权限控制角色计算
const isAdmin = computed(() => userStore.roles.includes('admin') || userStore.roles.includes('ROLE_ADMIN'))
const isPresident = computed(() => userStore.roles.includes('president') || userStore.roles.includes('ROLE_PRESIDENT'))
const isDirector = computed(() => userStore.roles.includes('director') || userStore.roles.includes('ROLE_DIRECTOR'))

// 退出登录
const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}

// 主题切换
const { currentTheme, toggleTheme } = useTheme()
const isDark = computed(() => currentTheme.value === 'dark')

// 追踪最近访问
const trackVisit = (r: any) => {
  if (r.path === '/' || r.path === '/portal') return // 过滤首页
  const visits = JSON.parse(localStorage.getItem('SM_RECENT_VISITS') || '[]')
  const newVisit = {
    path: r.path,
    title: r.meta?.title || r.name || r.path,
    icon: r.meta?.icon || 'Document',
    time: new Date().toLocaleString()
  }
  const filtered = visits.filter((v: any) => v.path !== r.path)
  filtered.unshift(newVisit)
  localStorage.setItem('SM_RECENT_VISITS', JSON.stringify(filtered.slice(0, 8)))
}

watch(() => route.path, () => {
  trackVisit(route)
}, { immediate: true })
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  &.is-embedded {
    background: #fff;
    height: auto;
    overflow: auto;
  }

  .top-header {
    background: #111827; // Dark Header (Slate 900)
    border-bottom: 1px solid #1f2937;
    color: #fff;
    z-index: 1000;
    position: relative;
    height: 64px !important;
    padding: 0;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);

    .header-inner {
      margin: 0 auto;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;

      .logo {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 20px;
        font-weight: 700;
        color: #fff;
        letter-spacing: 0.5px;
        min-width: 200px;

        .el-icon {
          font-size: 28px;
          color: var(--primary-color);
          filter: drop-shadow(0 0 8px rgba(13, 189, 168, 0.4));
        }
      }

      .nav-menu {
        flex: 1;
        display: flex;
        justify-content: center;
        height: 100%;

        .el-menu-horizontal {
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
      }

      .header-right {
        min-width: 200px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 16px;

        .theme-toggle-btn {
          background: rgba(255, 255, 255, 0.1);
          border: 1px solid rgba(255, 255, 255, 0.2);
          color: rgba(255, 255, 255, 0.8);
          transition: all 0.3s ease;

          &:hover {
            background: rgba(255, 255, 255, 0.15);
            border-color: var(--primary-color);
            color: var(--primary-color);
            transform: rotate(15deg) scale(1.1);
          }
        }

        .user-profile {
          display: flex;
          align-items: center;
          gap: 10px;
          cursor: pointer;
          transition: opacity 0.3s;

          &:hover {
            opacity: 0.8;
          }

          .el-dropdown-link {
            font-weight: 500;
            color: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
          }
        }
      }
    }
  }

  .main-content {
    flex: 1;
    padding: 0;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    overflow-x: hidden;

    &.is-embedded {
      background: transparent !important;
    }

    .content-wrapper {
      margin: 20px 0 0;
      padding: 0 24px 20px;
      flex: 1;
      width: 100%;
      display: flex;
      flex-direction: column;

      &.is-embedded {
        margin: 0;
        padding: 0;
        min-height: 100vh;
        background: #fff;
      }
    }
  }

  .footer {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    border-top: 1px solid #f0f0f0;
    color: var(--text-secondary);
    font-size: 13px;
    margin-top: auto;
  }
}
</style>
