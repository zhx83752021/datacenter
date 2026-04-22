import urllib.request
import json

url = "http://localhost:8080/api/test/validation/audit?periodDate=2026-03-09"
try:
    with urllib.request.urlopen(url) as response:
        body = response.read().decode('utf-8')
        data = json.loads(body)
        print(json.dumps(data, indent=2, ensure_ascii=False))
except Exception as e:
    print(f"Error: {e}")
