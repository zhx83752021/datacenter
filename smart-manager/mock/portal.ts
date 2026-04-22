// Portal Mock
export default [
    {
        url: '/api/portal/stats',
        method: 'get',
        response: (req: any) => {
            const auth = req.headers['authorization'] || '';
            const token = auth.replace('Bearer ', '');

            if (token === 'director-token') {
                return {
                    code: 200,
                    message: 'success',
                    data: [
                        { value: '3', label: '科室待办', trend: 'up', trendValue: '1' },
                        { value: '85.2%', label: '本月达成率', trend: 'up', trendValue: '5.2%' },
                        { value: '12', label: '今日门诊量', trend: 'down', trendValue: '2%' },
                        { value: '3', label: '科室科研' }
                    ]
                }
            }
            return {
                code: 200,
                message: 'success',
                data: [
                    { value: '8', label: '待处理预警', trend: 'up', trendValue: '2' },
                    { value: '127', label: '今日访问量', trend: 'up', trendValue: '12%' },
                    { value: '95.2%', label: '目标达成率', trend: 'down', trendValue: '1%' },
                    { value: '42', label: '在线设备' }
                ]
            }
        }
    },
    {
        url: '/api/portal/dashboards',
        method: 'get',
        response: (req: any) => {
            const auth = req.headers['authorization'] || '';
            const token = auth.replace('Bearer ', '');

            if (token === 'director-token') {
                return {
                    code: 200,
                    message: 'success',
                    data: [
                        { id: 10, title: '科室驾驶舱', desc: '本科室核心指标监测与工作安排', icon: 'TrendCharts', color: '#5C6BC0', path: '/cockpit/dept' },
                        { id: 2, title: '全景运营监控', desc: '多维度运营指标全景态势分析', icon: 'DataAnalysis', color: '#4FC3F7', path: '/monitor' },
                        { id: 3, title: '重点效率监控', desc: '效率类核心指标深度穿透监控', icon: 'PieChart', color: '#FFB84D', path: '/analysis/theme' },
                    ]
                }
            }
            return {
                code: 200,
                message: 'success',
                data: [
                    { id: 1, title: '院长驾驶舱', desc: '全院核心KPI实时监控与决策支持', icon: 'Monitor', color: '#0dbda8', path: '/cockpit' },
                    { id: 2, title: '全景运营监控', desc: '多维度运营指标全景态势分析', icon: 'DataAnalysis', color: '#4FC3F7', path: '/monitor' },
                    { id: 3, title: '重点效率监控', desc: '效率类核心指标深度穿透监控', icon: 'PieChart', color: '#FFB84D', path: '/analysis/theme' },
                ]
            }
        }
    },
    {
        url: '/api/portal/alerts',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: [
                    {
                        id: 1,
                        title: '药占比超标预警',
                        desc: '外科病区药占比达35.2%，超出30%标准警戒线。',
                        level: 'critical',
                        time: '10分钟前'
                    },
                    {
                        id: 2,
                        title: '床位使用率偏低',
                        desc: '内科病区床位使用率仅62%，建议调整收治策略。',
                        level: 'warning',
                        time: '2小时前'
                    },
                    {
                        id: 3,
                        title: '手术量数据更新',
                        desc: '本月由医务处提交的手术量统计报表已更新统计内容。',
                        level: 'info',
                        time: '4小时前'
                    },
                ]
            }
        }
    }
]
