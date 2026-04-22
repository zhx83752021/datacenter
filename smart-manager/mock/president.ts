// President Mock
export default [
    {
        url: '/api/cockpit/president/kpis',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: [
                    { label: '门诊总收入', value: '4,520,800', unit: '元', rate: '12.5%', status: 'up', percentage: 85, type: 'kpi-blue', chartColor: '#3B82F6', prefix: '¥', path: 'M0,15 Q25,5 50,15 T100,10', areaPath: 'M0,15 Q25,5 50,15 T100,10 L100,24 L0,24 Z' },
                    { label: '住院总收入', value: '8,120,500', unit: '元', rate: '8.2%', status: 'up', percentage: 72, type: 'kpi-teal', chartColor: '#0dbda8', prefix: '¥', path: 'M0,20 Q30,10 60,18 T100,12', areaPath: 'M0,20 Q30,10 60,18 T100,12 L100,24 L0,24 Z' },
                    { label: '平均住院日', value: '6.2', unit: '天', rate: '0.4%', status: 'down', percentage: 95, type: 'kpi-orange', chartColor: '#F59E0B', path: 'M0,10 L20,15 L40,8 L60,18 L80,12 L100,16', areaPath: 'M0,10 L20,15 L40,8 L60,18 L80,12 L100,16 L100,24 L0,24 Z' },
                    { label: '核心质控指标', value: '98.2', unit: '分', rate: '2.1%', status: 'up', percentage: 98, type: 'kpi-coral', chartColor: '#FF6B6B', path: 'M0,20 Q50,5 100,15', areaPath: 'M0,20 Q50,5 100,15 L100,24 L0,24 Z' },
                ]
            }
        }
    },
    {
        url: '/api/cockpit/president/trend',
        method: 'get',
        response: (config: any) => {
            const { type } = config.query
            const current = type === 'income' ? [4.2, 5.1, 3.8, 4.5, 6.2, 5.8, 5.2] : [3.8, 4.2, 3.5, 3.9, 4.8, 4.5, 4.1]
            const last = type === 'income' ? [3.8, 4.5, 3.2, 4.1, 5.2, 5.0, 4.8] : [3.5, 3.8, 3.1, 3.5, 4.2, 4.0, 3.8]
            return {
                code: 200,
                message: 'success',
                data: {
                    xAxis: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'],
                    current,
                    last
                }
            }
        }
    },
    {
        url: '/api/cockpit/president/income-composition',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: [
                    { name: '药品收入', value: 45, color: '#3B82F6' },
                    { name: '医疗服务', value: 30, color: '#0dbda8' },
                    { name: '检查检验', value: 15, color: '#F59E0B' },
                    { name: '耗材收入', value: 10, color: '#FF6B6B' },
                ]
            }
        }
    },
    {
        url: '/api/cockpit/president/rankings',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: [
                    { dept: '心血管内科', value: '1,205', yoy: 15.2 },
                    { dept: '呼吸内科', value: '1,102', yoy: 8.4 },
                    { dept: '儿科', value: '985', yoy: -2.1 },
                    { dept: '骨科', value: '856', yoy: 12.5 },
                    { dept: '普外科', value: '742', yoy: 5.8 },
                ]
            }
        }
    },
    {
        url: '/api/cockpit/president/efficiency',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: [
                    { label: '床位周转率', value: '1.25', icon: 'TrendCharts', colorType: 'blue', colorCode: '#3B82F6', percentValue: 85 },
                    { label: '药占比控制', value: '32.5%', icon: 'FirstAidKit', colorType: 'teal', colorCode: '#0dbda8', percentValue: 72 },
                    { label: '手术及时率', value: '95.8%', icon: 'Timer', colorType: 'orange', colorCode: '#F59E0B', percentValue: 95 },
                    { label: '患者满意度', value: '4.8', icon: 'Aim', colorType: 'coral', colorCode: '#FF6B6B', percentValue: 96 },
                ]
            }
        }
    }
]
