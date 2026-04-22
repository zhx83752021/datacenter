SELECT v.period_date, l.code, l.name, v.value
FROM sm_indicator_value v
JOIN sm_indicator_lib l ON v.indicator_id = l.id
WHERE l.code LIKE 'QUA017%' AND v.period_date = '2026-03';
