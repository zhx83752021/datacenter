<template>
  <el-container class="layout-container is-frontend" :class="{ 'is-embedded': isEmbedded }">
    <el-header v-if="!isEmbedded" class="top-header">
      <div class="header-inner">
        <div class="header-left-cluster">
          <el-button
            v-if="isMobile"
            text
            circle
            class="mobile-menu-trigger"
            aria-label="打开导航菜单"
            @click="mobileNavOpen = true"
          >
            <el-icon :size="22">
              <Menu />
            </el-icon>
          </el-button>

          <div class="logo">
            <el-icon :size="28" color="#0dbda8">
              <Monitor />
            </el-icon>
            <span class="title">智慧管理平台</span>
          </div>
        </div>

        <div v-if="!isMobile" class="nav-menu">
          <MainNavMenu mode="horizontal" />
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
                <span class="user-name-text">{{ userStore.name || 'Admin' }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
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

    <el-drawer
      v-if="isMobile"
      v-model="mobileNavOpen"
      direction="ltr"
      size="86%"
      class="mobile-nav-drawer"
      title="导航菜单"
      append-to-body
    >
      <MainNavMenu mode="vertical" @menu-select="mobileNavOpen = false" />
    </el-drawer>

    <el-main class="main-content">
      <div class="content-wrapper" :class="{ 'is-embedded': isEmbedded, 'is-mobile': isMobile }">
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
  Monitor, Sunny, Moon, ArrowDown, Menu
} from '@element-plus/icons-vue'
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBreakpoint } from '../composables/useBreakpoint'
import { useTheme } from '../composables/useTheme'
import { useUserStore } from '../stores/user'
import MainNavMenu from './MainNavMenu.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isMobile } = useBreakpoint()
const isEmbedded = computed(() => route.query.embedded === 'true')
const mobileNavOpen = ref(false)

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
  mobileNavOpen.value = false
}, { immediate: true })

watch(isMobile, (m) => {
  if (!m) mobileNavOpen.value = false
})
</script>

<style scoped lang="scss">
.layout-container {
  min-height: 100vh;
  min-height: 100dvh;
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  &.is-embedded {
    background: #fff;
    height: auto;
    min-height: auto;
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
      gap: 12px;
      min-width: 0;

      .header-left-cluster {
        display: flex;
        align-items: center;
        gap: 4px;
        min-width: 0;
        flex-shrink: 0;
      }

      .mobile-menu-trigger {
        flex-shrink: 0;
        color: #fff !important;
        min-width: 44px;
        min-height: 44px;
        padding: 0;

        &:hover {
          background: rgba(255, 255, 255, 0.08) !important;
        }
      }

      .logo {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 20px;
        font-weight: 700;
        color: #fff;
        letter-spacing: 0.5px;
        min-width: 0;

        .title {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .el-icon {
          font-size: 28px;
          flex-shrink: 0;
          color: var(--primary-color);
          filter: drop-shadow(0 0 8px rgba(13, 189, 168, 0.4));
        }
      }

      .nav-menu {
        flex: 1;
        display: flex;
        justify-content: center;
        height: 100%;
        min-width: 0;
      }

      .header-right {
        min-width: 0;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 16px;
        flex-shrink: 0;

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
          min-width: 0;

          &:hover {
            opacity: 0.8;
          }

          .el-dropdown-link {
            font-weight: 500;
            color: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
            max-width: 140px;
          }

          .user-name-text {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }
    }
  }

  @media (max-width: 768px) {
    .top-header .header-inner {
      padding: 0 12px;

      .logo {
        font-size: 16px;
        gap: 8px;
      }
    }
  }

  /* 移动端抽屉导航：限制内边距，避免菜单区域产生横向滚动条 */
  :deep(.mobile-nav-drawer .el-drawer__body) {
    padding: 8px 10px 16px;
    overflow-x: hidden;
    box-sizing: border-box;
  }

  .main-content {
    flex: 1;
    padding: 0;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    overflow-x: hidden;
    min-height: 0;

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
        min-height: 100dvh;
        background: #fff;
      }

      &.is-mobile {
        margin-top: 12px;
        padding: 0 12px 16px;
      }
    }
  }

  .footer {
    height: auto;
    min-height: 48px;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    border-top: 1px solid #f0f0f0;
    color: var(--text-secondary);
    font-size: 13px;
    margin-top: auto;
    text-align: center;

    .copyright {
      line-height: 1.4;
    }
  }
}
</style>
