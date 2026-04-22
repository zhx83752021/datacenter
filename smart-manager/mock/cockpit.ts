// Cockpit Mock
export default [
    {
        url: '/api/cockpit/dept/metrics/:deptId',
        method: 'get',
        response: (config: any) => {
            const { deptId } = config.params
            const seed = deptId.length
            return {
                code: 200,
                message: 'success',
                data: [
                    { title: '今日挂号', value: (1000 + seed * 50).toLocaleString(), unit: '人', icon: 'User', colorType: 'blue', trendPath: 'M0,15 Q30,5 50,15 T100,10' },
                    { title: '在院患者', value: (80 + seed).toString(), unit: '人', icon: 'House', colorType: 'green', trendPath: 'M0,20 Q40,10 60,18 T100,12' },
                    { title: '今日手术', value: (10 + seed % 5).toString(), unit: '台', icon: 'Operation', colorType: 'orange', trendPath: 'M0,15 L20,20 L30,5 L50,15 L70,8 L80,18 L100,10' },
                    { title: '床位使用率', value: (90 + seed % 10).toFixed(1), unit: '%', icon: 'DataLine', colorType: 'red', trendPath: 'M0,20 Q50,30 100,15' },
                ]
            }
        }
    },
    {
        url: '/api/cockpit/dept/trend/:deptId',
        method: 'get',
        response: (config: any) => {
            const { type } = config.query
            const { deptId } = config.params
            const base = deptId === 'cardio' ? 100 : 50
            const data = type === 'outpatient'
                ? [base + 20, base + 32, base + 1, base + 34, base + 90, base + 130, base + 110]
                : [base - 10, base - 5, base - 12, base + 1, base - 5, base + 12, base + 8]
            return {
                code: 200,
                message: 'success',
                data: {
                    days: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
                    values: data
                }
            }
        }
    },
    {
        url: '/api/cockpit/dept/doctors/:deptId',
        method: 'get',
        response: (config: any) => {
            const { deptId } = config.params
            const names: any = {
                cardio: ['张心德', '李脉动', '王血管', '赵心律', '陈起搏'],
                ortho: ['骨折张', '李关节', '王脊柱', '赵韧带', '陈石膏'],
                general: ['陈手术', '王开腹', '李胆囊', '赵胰腺', '陈阑尾'],
                resp: ['钟呼吸', '李肺科', '王气管', '赵支气管', '陈纤支镜']
            }
            const currentNames = names[deptId] || names.cardio
            return {
                code: 200,
                message: 'success',
                data: currentNames.map((name: string, i: number) => ({
                    name,
                    title: i < 2 ? '主任医师' : '副主任医师',
                    outpatient: 80 + i * 20,
                    surgery: 10 - i,
                    income: 20 + i * 5
                }))
            }
        }
    },
    {
        url: '/api/cockpit/dept/rankings',
        method: 'get',
        response: (config: any) => {
            const { metric } = config.query
            const mockValues: any = {
                income: [85, 92, 110, 125, 140],
                cmi: [0.9, 1.0, 1.1, 1.25, 1.35],
                efficiency: [78, 82, 85, 90, 95]
            }
            return {
                code: 200,
                message: 'success',
                data: {
                    categories: ['儿科', '普外科', '骨科', '呼吸内科', '心血管内科'],
                    values: mockValues[metric] || mockValues.income
                }
            }
        }
    },
    {
        url: '/api/cockpit/president/kpis',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: [
                    { label: '门诊总收入', value: '45,231,000', unit: '元', type: 'kpi-blue', status: 'up', rate: '12.5%', percentage: 85, chartColor: '#3B82F6', areaPath: 'M0,20 Q50,5 100,15 L100,24 L0,24 Z', path: 'M0,20 Q50,5 100,15' },
                    { label: '住院总收入', value: '128,450,000', unit: '元', type: 'kpi-teal', status: 'up', rate: '8.2%', percentage: 92, chartColor: '#0dbda8', areaPath: 'M0,15 Q30,25 70,10 T100,18 L100,24 L0,24 Z', path: 'M0,15 Q30,25 70,10 T100,18' },
                    { label: '均次费用', value: '1,280.5', unit: '元', type: 'kpi-orange', status: 'down', rate: '2.1%', percentage: 78, chartColor: '#F59E0B', areaPath: 'M0,10 L20,15 L40,8 L60,18 L80,12 L100,20 L100,24 L0,24 Z', path: 'M0,10 L20,15 L40,8 L60,18 L80,12 L100,20' },
                    { label: '药占比', value: '28.4', unit: '%', type: 'kpi-coral', status: 'down', rate: '1.5%', percentage: 95, chartColor: '#FF6B6B', areaPath: 'M0,20 Q50,30 100,15 L100,24 L0,24 Z', path: 'M0,20 Q50,30 100,15' },
                ]
            }
        }
    },
    {
        url: '/api/cockpit/president/trend',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: {
                    xAxis: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    current: [4200, 4800, 4500, 5200, 5800, 6100],
                    last: [3800, 4100, 4300, 4600, 5000, 5200]
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
                    { name: '药品收入', value: 45, color: '#0dbda8' },
                    { name: '手术收入', value: 25, color: '#3B82F6' },
                    { name: '检查收入', value: 15, color: '#F59E0B' },
                    { name: '护理收入', value: 15, color: '#FF6B6B' }
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
                    { dept: '心血管内科', value: '1,200', yoy: 15.2 },
                    { dept: '神经外科', value: '1,050', yoy: 12.8 },
                    { dept: '骨科', value: '980', yoy: -5.4 },
                    { dept: '呼吸内科', value: '850', yoy: 8.5 },
                    { dept: '儿科', value: '720', yoy: 2.1 }
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
                    { label: '床位使用率', value: '92.5%', percentValue: 92.5, icon: 'Histogram', colorType: 'blue', colorCode: '#3B82F6' },
                    { label: '平均住院日', value: '6.8天', percentValue: 85, icon: 'Timer', colorType: 'teal', colorCode: '#0dbda8' },
                    { label: '出院人次', value: '8,452', percentValue: 90, icon: 'User', colorType: 'orange', colorCode: '#F59E0B' },
                    { label: '手术台数', value: '1,254', percentValue: 75, icon: 'FirstAidKit', colorType: 'coral', colorCode: '#FF6B6B' }
                ]
            }
        }
    }
]
