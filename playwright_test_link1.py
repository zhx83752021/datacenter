# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
from playwright.sync_api import sync_playwright

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(args=["--window-size=1920,1080"], headless=True)
        context = browser.new_context(viewport={"width": 1920, "height": 1080})
        page = context.new_page()

        print("[1] Opening login page...")
        page.goto('http://localhost:8080/login')
        page.wait_for_load_state('domcontentloaded')

        print("[2] Filling credentials...")
        page.fill("input[placeholder='请输入用户名']", "admin")
        page.fill("input[placeholder='请输入密码']", "123456")

        print("[3] Clicking login...")
        page.click("button.submit-btn")

        print("[4] Waiting for navigation to dashboard...")
        page.wait_for_timeout(2000) # give it a moment to login
        page.wait_for_load_state('domcontentloaded')

        print("[5] Navigating to Indicator Lib (/monitor/lib)...")
        # Ensure we are in the system, and can navigate.
        page.goto('http://localhost:8080/monitor/lib')
        page.wait_for_timeout(2000)
        page.wait_for_load_state('domcontentloaded')

        print("[6] Finding '批量导入' button...")
        # Search for the button using CSS in the .actions group
        import_button = page.locator(".actions button.is-circle").first
        if import_button.count() == 0:
            import_button = page.locator(".actions button").nth(1)

        import_button.click()
        print("[7] Clicked '批量导入', waiting for dialog...")
        page.wait_for_timeout(1000)

        print("[8] Verifying dialog content...")
        dialog = page.locator(".el-dialog, .el-overlay").filter(has_text="批量指标导入")
        if dialog.count() == 0:
            # Fallback
            dialog = page.locator(".import-dialog")
        if dialog.count() == 0:
            print("ERROR: Dialog not found.")
            page.screenshot(path="e:/datacenter2/error_dialog_not_found.png")
            return

        dialog_text = dialog.first.inner_text()
        print("--- Dialog Text ---")
        print(dialog_text)
        print("-------------------")

        if "下载指标模版" in dialog_text or ".csv" in dialog_text:
            print("Success: Found '下载指标模版' text.")
        else:
            print("Warning: Could not find '下载指标模版' in dialog.")

        if "上传数据文件" in dialog_text or "上传" in dialog_text:
            print("Success: Found '上传数据文件' text.")
        else:
            print("Warning: Could not find '上传数据文件' in dialog.")

        print("Taking final screenshot...")
        page.screenshot(path="e:/datacenter2/success_import_dialog.png")

        browser.close()

if __name__ == '__main__':
    run()
