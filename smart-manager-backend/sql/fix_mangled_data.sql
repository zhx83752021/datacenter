UPDATE sm_indicator_lib SET name = '急危重症抢救成功率(分子)' WHERE code = 'QUA014_N';
UPDATE sm_indicator_lib SET name = '急危重症抢救成功率(分母)' WHERE code = 'QUA014_D';
UPDATE sm_indicator_lib SET name = '患者入院8h内委派率(分子)' WHERE code = 'QUA002_N';
UPDATE sm_indicator_lib SET name = '全院总收入' WHERE code = 'ECO001';
UPDATE sys_user SET real_name = '王院长' WHERE username = 'president';
UPDATE sys_user SET real_name = '李主任' WHERE username = 'director_li';
UPDATE sys_role SET role_name = '管理层' WHERE role_key = 'president';
