<template>
  <div class="login-container">
    <!-- Background Effects -->
    <div class="bg-effect bg-1"></div>
    <div class="bg-effect bg-2"></div>
    <div class="bg-effect bg-3"></div>

    <div class="login-content">
      <div class="brand-section">
        <div class="logo-wrapper">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="url(#logo-grad)" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
            <path d="M2 17L12 22L22 17" stroke="url(#logo-grad)" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
            <path d="M2 12L12 17L22 12" stroke="url(#logo-grad)" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
            <defs>
              <linearGradient id="logo-grad" x1="2" y1="2" x2="22" y2="22" gradientUnits="userSpaceOnUse">
                <stop stop-color="#4facfe" />
                <stop offset="1" stop-color="#00f2fe" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="main-title">智慧管理平台</h1>
        <!-- <p class="sub-title">Smart Management Platform</p> -->
      </div>

      <div class="login-card glass-panel">
        <div class="card-header">
          <h2>用户登录</h2>
          <p>欢迎回来，请验证身份</p>
        </div>

        <el-form :model="form" class="login-form" @submit.prevent>
          <div class="inputs-container">
            <div class="input-group">
              <el-input v-model="form.username" placeholder="请输入用户名" class="glass-input">
                <template #prefix>
                  <el-icon>
                    <User />
                  </el-icon>
                </template>
              </el-input>
            </div>

            <div class="input-group">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password class="glass-input"
                @keyup.enter="handleLogin">
                <template #prefix>
                  <el-icon>
                    <Lock />
                  </el-icon>
                </template>
              </el-input>
            </div>
          </div>

          <div class="actions">
            <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
              登录系统
            </el-button>
          </div>

          <div class="account-tips">
            <div class="tips-title">测试账号 (密码: 123456)</div>
            <div class="accounts-list">
              <el-tag size="small" effect="plain" class="account-tag" @click="fillAccount('admin')">admin (管理员)</el-tag>
              <el-tag size="small" effect="plain" class="account-tag" @click="fillAccount('president')">president (院长)</el-tag>
              <el-tag size="small" effect="plain" class="account-tag" @click="fillAccount('director_li')">director_li (主任)</el-tag>
              <el-tag size="small" effect="plain" class="account-tag" @click="fillAccount('wangwu')">wangwu (普通用户)</el-tag>
            </div>
          </div>
        </el-form>
      </div>

      <div class="footer">
        <p>© 2026 智慧管理平台 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const form = ref({
  username: 'admin',
  password: '123456'
})

const loading = ref(false)

const fillAccount = (username: string) => {
  form.value.username = username
  form.value.password = '123456'
}

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    await userStore.login(form.value)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (error: any) {
    console.error(error)
    const msg = error.message || '登录失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '@fontsource/plus-jakarta-sans/index.css';

/* Background System */
/* Background System */
.login-container {
  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  background-color: #0f172a;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Plus Jakarta Sans', sans-serif;

  /* Grid Pattern via pseudo-element to control layering */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image:
      linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
    background-size: 50px 50px;
    z-index: 1;
    /* Grid above base, below glows */
    pointer-events: none;
  }
}

/* Atmospheric Glows */
.bg-effect {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  /* Slightly reduced blur for more definition */
  z-index: 2;
  /* Glows above grid */
  pointer-events: none;
  mix-blend-mode: screen;
  /* Makes them glow */
}

.bg-1 {
  width: 700px;
  height: 700px;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.5) 0%, rgba(59, 130, 246, 0.2) 70%);
  /* Increased opacity */
  top: -150px;
  left: -150px;
  animation: float-1 15s infinite ease-in-out alternate;
}

.bg-2 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(167, 139, 250, 0.5) 0%, rgba(236, 72, 153, 0.2) 70%);
  /* Increased opacity and tweaked purple */
  bottom: -100px;
  right: -100px;
  animation: float-2 12s infinite ease-in-out alternate;
}

.bg-3 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(52, 211, 153, 0.3) 0%, rgba(6, 182, 212, 0.15) 70%);
  /* Increased opacity */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse-glow 8s infinite ease-in-out;
}

/* Animations */
@keyframes float-1 {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }

  100% {
    transform: translate(50px, 50px) rotate(10deg);
  }
}

@keyframes float-2 {
  0% {
    transform: translate(0, 0) scale(1);
  }

  100% {
    transform: translate(-30px, -30px) scale(1.1);
  }
}

@keyframes pulse-glow {

  0%,
  100% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }

  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

.login-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
  width: 100%;
  max-width: 440px;
  padding: 2rem;
}

.brand-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 2rem;

  .logo-wrapper {
    width: 56px;
    height: 56px;
    margin: 0;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

    .logo-icon {
      width: 32px;
      height: 32px;
      filter: drop-shadow(0 0 8px rgba(79, 172, 254, 0.5));
    }
  }

  .main-title {
    font-size: 2.2rem;
    font-weight: 800;
    color: #ffffff;
    margin: 0;
    letter-spacing: -0.02em;
    background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .sub-title {
    color: #94a3b8;
    margin-top: 0.5rem;
    font-size: 0.95rem;
    font-weight: 500;
    letter-spacing: 0.05em;
    text-transform: uppercase;
  }
}

.glass-panel {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 2.5rem;
  width: 100%;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.6);
    border-color: rgba(255, 255, 255, 0.12);
  }
}

.card-header {
  text-align: center;
  margin-bottom: 2rem;

  h2 {
    color: #fff;
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0 0 0.5rem;
  }

  p {
    color: #64748b;
    font-size: 0.9rem;
    margin: 0;
  }
}

.login-form {
  .inputs-container {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
    margin-bottom: 2rem;
  }
}

:deep(.glass-input) {
  .el-input__wrapper {
    background: rgba(15, 23, 42, 0.4);
    box-shadow: none !important;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 8px 16px;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(255, 255, 255, 0.2);
      background: rgba(15, 23, 42, 0.5);
    }

    &.is-focus {
      border-color: #4facfe;
      background: rgba(15, 23, 42, 0.6);
      box-shadow: 0 0 0 1px #4facfe !important;
    }
  }

  .el-input__inner {
    color: #ffffff;
    height: 44px;
    font-size: 1rem;

    &::placeholder {
      color: #475569;
    }
  }

  .el-input__prefix {
    color: #64748b;
    margin-right: 8px;
  }

  .el-icon {
    font-size: 1.1rem;
  }
}

.actions {
  .submit-btn {
    width: 100%;
    height: 52px;
    font-size: 1.05rem;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border: none;
    color: #fff;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(rgba(255, 255, 255, 0.2), transparent);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 8px 20px rgba(0, 242, 254, 0.3);

      &::before {
        opacity: 1;
      }
    }

    &:active {
      transform: translateY(0);
    }
  }
}

.account-tips {
  margin-top: 1.5rem;
  text-align: center;

  .tips-title {
    color: #94a3b8;
    font-size: 0.85rem;
    margin-bottom: 0.8rem;
  }

  .accounts-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    justify-content: center;

    .account-tag {
      cursor: pointer;
      background: rgba(255, 255, 255, 0.05);
      border-color: rgba(255, 255, 255, 0.1);
      color: #cbd5e1;
      transition: all 0.3s ease;

      &:hover {
        background: rgba(79, 172, 254, 0.2);
        border-color: #4facfe;
        color: #fff;
      }
    }
  }
}

.footer {
  text-align: center;

  p {
    color: #475569;
    font-size: 0.8rem;
    font-weight: 500;
  }
}

/* Responsive Handling */
@media (max-width: 480px) {
  .login-content {
    padding: 1.5rem;
  }

  .glass-panel {
    padding: 2rem 1.5rem;
  }

  .bg-1,
  .bg-2 {
    width: 300px;
    height: 300px;
  }
}
</style>
