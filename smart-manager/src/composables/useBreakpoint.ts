import { onMounted, onUnmounted, ref } from 'vue'

/** 与 docs/MOBILE_RESPONSIVE_PLAN.md 保持一致 */
export const BREAKPOINT_MOBILE_MAX = 768
export const BREAKPOINT_TABLET_MAX = 1024

function getInitialWidth() {
  return typeof window !== 'undefined' ? window.innerWidth : 1200
}

export function useBreakpoint() {
  const width = ref(getInitialWidth())
  const isMobile = ref(matchMobile(width.value))
  const isTablet = ref(matchTablet(width.value))

  function refresh() {
    if (typeof window === 'undefined') return
    const w = window.innerWidth
    width.value = w
    isMobile.value = matchMobile(w)
    isTablet.value = matchTablet(w)
  }

  onMounted(() => {
    refresh()
    window.addEventListener('resize', refresh, { passive: true })
  })

  onUnmounted(() => {
    window.removeEventListener('resize', refresh)
  })

  return {
    width,
    isMobile,
    isTablet,
    BREAKPOINT_MOBILE_MAX,
    BREAKPOINT_TABLET_MAX,
  }
}

function matchMobile(w: number) {
  return w <= BREAKPOINT_MOBILE_MAX
}

function matchTablet(w: number) {
  return w > BREAKPOINT_MOBILE_MAX && w <= BREAKPOINT_TABLET_MAX
}
