import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="root_password",
        database="smart_manager"
    )
    cursor = conn.cursor(dictionary=True)

    query = """
    SELECT v.period_date, l.code, l.name, v.value
    FROM sm_indicator_value v
    JOIN sm_indicator_lib l ON v.indicator_id = l.id
    WHERE l.code LIKE 'QUA020%' AND v.period_date = '2026-03';
    """
    cursor.execute(query)
    rows = cursor.fetchall()

    for row in rows:
        print(f"[{row['period_date']}] {row['code']} - {row['name']}: {row['value']}")

    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")
