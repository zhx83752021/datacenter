/// <reference types="vite/client" />

declare module '*.vue' {
    import type { DefineComponent } from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
}

declare module 'element-plus/dist/locale/zh-cn.mjs'

// Fix for echarts if types are missing (though usually they are present in v5)
declare module 'echarts' {
    const echarts: any;
    export = echarts;
}
