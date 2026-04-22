// Monitor Mock
export default [
    {
        url: '/api/monitor/dashboard',
        method: 'get',
        response: (config: any) => {
            const { theme, campus, keyword } = config.query
            const auth = config.headers['authorization'] || '';
            const token = auth.replace('Bearer ', '');

            let data = [
                { id: 1, code: 'OUTPATIENT_COUNT', name: '门急诊总人次', category: '运营效率', theme: 'efficiency', value: '45,231', unit: '人次', mom: 5.2, yoy: 12.1, color: '#0dbda8', icon: 'UserFilled', campus: 'main' },
                { id: 2, code: 'INCOME_TOTAL', name: '医疗总收入', category: '财务经济', theme: 'finance', value: '3,200', unit: '万元', mom: -1.2, yoy: 8.5, color: '#4FC3F7', icon: 'Money', campus: 'main' },
                { id: 3, code: 'INCOME_MED', name: '平均住院日', category: '医疗质量', theme: 'quality', value: '6.5', unit: '天', mom: -0.5, yoy: -4.2, color: '#FFB84D', icon: 'Timer', campus: 'main', dept: 'inner' },
                { id: 4, code: 'DRUG_RATIO', name: '药占比', category: '合理用药', theme: 'quality', value: '28.4', unit: '%', mom: 0.2, yoy: -1.5, color: '#FF6B6B', icon: 'FirstAidKit', campus: 'main' },
                { id: 5, code: 'INCOME_DRUG', name: '四级手术率', category: '医疗技术', theme: 'quality', value: '18.2', unit: '%', mom: 1.2, yoy: 3.4, color: '#0dbda8', icon: 'Operation', campus: 'main' },
                { id: 6, code: 'INCOME_OTHER', name: '患者满意度', category: '服务评价', theme: 'service', value: '98.2', unit: '分', mom: 0.1, yoy: 0.5, color: '#81C784', icon: 'StarFilled', campus: 'main' },
                { id: 7, code: 'OUTPATIENT_COUNT', name: '床位使用率', category: '运营效率', theme: 'efficiency', value: '92.5', unit: '%', mom: 2.3, yoy: 5.1, color: '#4FC3F7', icon: 'Histogram', campus: 'east' },
                { id: 8, code: 'DRUG_RATIO', name: '耗占比', category: '合理用药', theme: 'quality', value: '32.1', unit: '%', mom: -0.8, yoy: -2.3, color: '#FFAB91', icon: 'Box', campus: 'east' },
                { id: 9, code: 'INPATIENT_COUNT', name: '出院人次', category: '运营效率', theme: 'efficiency', value: '8,452', unit: '人次', mom: 3.5, yoy: 7.8, color: '#0dbda8', icon: 'User', campus: 'east' },
                { id: 10, code: 'INCOME_MED', name: '手术台数', category: '医疗技术', theme: 'quality', value: '1,254', unit: '台', mom: 4.2, yoy: 9.6, color: '#FF6B6B', icon: 'Operation', campus: 'main' },
                { id: 11, code: 'INCOME_DRUG', name: '门诊收入', category: '财务经济', theme: 'finance', value: '1,850', unit: '万元', mom: 2.1, yoy: 6.3, color: '#4FC3F7', icon: 'CreditCard', campus: 'east' },
                { id: 12, code: 'INPATIENT_COUNT', name: '病床周转次数', category: '运营效率', theme: 'efficiency', value: '28.5', unit: '次', mom: 1.8, yoy: 4.5, color: '#FFB84D', icon: 'Refresh', campus: 'main' },
            ]

            // 科室主任仅能看到科室相关指标
            if (token === 'director-token') {
                data = data.filter(i => ['efficiency', 'quality'].includes(i.theme as string))
            }

            if (theme && theme !== 'all') data = data.filter(i => i.theme === theme)
            if (campus) data = data.filter(i => i.campus === campus)
            if (keyword) data = data.filter(i => i.name.includes(keyword))

            return { code: 200, message: 'success', data }
        }
    },
    {
        url: '/api/monitor/trend',
        method: 'get',
        response: (config: any) => {
            const { theme } = config.query
            let seriesData = [150, 230, 224, 218, 135, 147]
            if (theme === 'quality') seriesData = [98.5, 98.2, 98.8, 99.1, 98.9, 99.2]
            else if (theme === 'efficiency') seriesData = [85, 88, 87, 92, 90, 95]
            else if (theme === 'finance') seriesData = [2800, 2900, 3100, 3050, 3200, 3400]

            return {
                code: 200,
                message: 'success',
                data: {
                    xAxis: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    seriesData
                }
            }
        }
    },
    {
        url: '/api/monitor/indicator/:id/drill',
        method: 'get',
        response: (config: any) => {
            return {
                code: 200,
                message: 'success',
                data: {
                    definition: '指标详细定义描述内容...',
                    formula: '[收入] / [人次]',
                    targetValue: 100,
                    status: 'normal'
                }
            }
        }
    },
    {
        url: '/api/monitor/indicator/history',
        method: 'get',
        response: (config: any) => {
            return {
                code: 200,
                message: 'success',
                data: {
                    xAxis: ['2025-01', '2025-02', '2025-03', '2025-04', '2025-05', '2025-06', '2025-07', '2025-08', '2025-09', '2025-10', '2025-11', '2025-12'],
                    seriesData: [120, 132, 101, 134, 90, 230, 210, 182, 191, 234, 290, 330],
                    targetData: [150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150]
                }
            }
        }
    },
    {
        url: '/api/monitor/indicator/ranking',
        method: 'get',
        response: (config: any) => {
            return {
                code: 200,
                message: 'success',
                data: {
                    depts: ['心内科', '呼吸内科', '消化内科', '外科', '儿科', '妇产科'],
                    values: [450, 420, 380, 350, 310, 280]
                }
            }
        }
    },
    {
        url: '/api/monitor/yoy',
        method: 'post',
        response: (config: any) => {
            return {
                code: 200,
                message: 'success',
                data: {
                    trendBase: [
                        { period: '2025-03', value: 120 },
                        { period: '2025-04', value: 132 },
                        { period: '2025-05', value: 101 },
                        { period: '2025-06', value: 134 },
                        { period: '2025-07', value: 155 },
                        { period: '2025-08', value: 180 },
                        { period: '2025-09', value: 160 },
                        { period: '2025-10', value: 190 },
                        { period: '2025-11', value: 210 },
                        { period: '2025-12', value: 240 },
                        { period: '2026-01', value: 220 },
                        { period: '2026-02', value: 235 }
                    ],
                    averageValue: 160,
                    yoy: 15.6,
                    mom: 5.2
                }
            }
        }
    },
    {
        url: '/api/monitor/composition',
        method: 'post',
        response: (config: any) => {
            const { indicatorCode } = config.body || {};

            // 默认返回一个模拟的构成树
            return {
                code: 200,
                message: 'success',
                data: {
                    name: indicatorCode || '当前指标',
                    isComposite: true,
                    formula: 'A + B + C',
                    children: [
                        {
                            name: '门诊部贡献',
                            isComposite: false,
                            value: '45.2%'
                        },
                        {
                            name: '急诊部贡献',
                            isComposite: false,
                            value: '12.8%'
                        },
                        {
                            name: '住院部计算因子',
                            isComposite: true,
                            formula: 'D * E',
                            children: [
                                { name: '普外科基数', isComposite: false, value: '280' },
                                { name: '产科权重', isComposite: false, value: '1.2' }
                            ]
                        }
                    ]
                }
            }
        }
    }
]
