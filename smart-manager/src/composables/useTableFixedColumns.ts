import { computed } from 'vue'
import { useBreakpoint } from './useBreakpoint'

/**
 * 窄屏下关闭 el-table 的 fixed 列（避免占宽与叠层），桌面端保留左右固定。
 * 外层容器：桌面 `sys-table-shell`，移动端 `table-responsive table-responsive--grow`。
 */
export function useTableFixedColumns() {
  const { isMobile } = useBreakpoint()

  const fixedLeft = computed<'left' | false>(() => (isMobile.value ? false : 'left'))
  const fixedRight = computed<'right' | false>(() => (isMobile.value ? false : 'right'))

  const tableShellClass = computed(() =>
    isMobile.value ? 'table-responsive table-responsive--grow' : 'sys-table-shell'
  )

  return { isMobile, fixedLeft, fixedRight, tableShellClass }
}
