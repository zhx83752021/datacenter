import { ref, watch, onMounted } from 'vue'

type Theme = 'light' | 'dark'

const THEME_KEY = 'smart-manager-theme'

// 全局主题状态
const currentTheme = ref<Theme>('light')

export function useTheme() {
  // 初始化主题
  const initTheme = () => {
    const savedTheme = localStorage.getItem(THEME_KEY) as Theme
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

    currentTheme.value = savedTheme || (prefersDark ? 'dark' : 'light')
    applyTheme(currentTheme.value)
  }

  // 应用主题
  const applyTheme = (theme: Theme) => {
    document.documentElement.setAttribute('data-theme', theme)

    // 更新body类名以便全局样式生效
    if (theme === 'dark') {
      document.body.classList.add('dark-theme')
      document.body.classList.remove('light-theme')
    } else {
      document.body.classList.add('light-theme')
      document.body.classList.remove('dark-theme')
    }
  }

  // 切换主题
  const toggleTheme = () => {
    currentTheme.value = currentTheme.value === 'light' ? 'dark' : 'light'
    localStorage.setItem(THEME_KEY, currentTheme.value)
    applyTheme(currentTheme.value)
  }

  // 设置特定主题
  const setTheme = (theme: Theme) => {
    currentTheme.value = theme
    localStorage.setItem(THEME_KEY, theme)
    applyTheme(theme)
  }

  // 监听系统主题变化
  onMounted(() => {
    initTheme()

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const handleChange = (e: MediaQueryListEvent) => {
      if (!localStorage.getItem(THEME_KEY)) {
        setTheme(e.matches ? 'dark' : 'light')
      }
    }

    mediaQuery.addEventListener('change', handleChange)

    return () => {
      mediaQuery.removeEventListener('change', handleChange)
    }
  })

  return {
    currentTheme,
    toggleTheme,
    setTheme,
    isDark: () => currentTheme.value === 'dark'
  }
}
