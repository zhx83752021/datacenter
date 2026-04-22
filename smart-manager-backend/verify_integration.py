import urllib.request
import json
import time

def call_api(url, method='GET'):
    print(f"Calling {method} {url}...")
    try:
        req = urllib.request.Request(url, method=method)
        with urllib.request.urlopen(req) as response:
            body = response.read().decode('utf-8')
            return json.loads(body)
    except Exception as e:
        print(f"Error calling {url}: {e}")
        return None

def main():
    # 1. Trigger Sync
    sync_url = "http://localhost:8081/api/sm/etl/sync?periodDate=2026-03"
    sync_res = call_api(sync_url, 'POST')
    if sync_res:
        print(f"Sync Result: {sync_res.get('msg')}")

    # 2. Wait a bit for async tasks if any (though controller seems synchronous)
    time.sleep(2)

    # 3. Audit Verification
    audit_url = "http://localhost:8081/api/test/validation/audit?periodDate=2026-03"
    audit_res = call_api(audit_url)

    if audit_res and audit_res.get('code') == 200:
        report = audit_res.get('data', [])
        print("\n=== Data Accuracy Integration Audit Report (2026-03) ===")
        print(f"{'Code':<12} | {'Name':<25} | {'Source':<10} | {'Value':<12} | {'Status'}")
        print("-" * 80)
        for item in report:
            status = item.get('status')
            val = str(item.get('value'))
            status_text = "[OK] SUCCESS" if status == "SUCCESS" else "[FAIL] " + status
            print(f"{item.get('code'):<12} | {item.get('name'):<25} | {item.get('source'):<10} | {val:<12} | {status_text}")

        with open("audit_result.json", "w", encoding="utf-8") as f:
            json.dump(report, f, indent=2, ensure_ascii=False)
        print("\nFull audit report saved to audit_result.json")
    else:
        print("Failed to retrieve audit report.")

if __name__ == "__main__":
    main()


