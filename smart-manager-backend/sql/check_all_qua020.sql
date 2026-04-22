SELECT v.period_date, l.code, l.name, v.value, v.dept_code
FROM sm_indicator_value v
JOIN sm_indicator_lib l ON v.indicator_id = l.id
WHERE l.code LIKE 'QUA020%'
ORDER BY v.period_date DESC;
