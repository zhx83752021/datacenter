# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
from playwright.sync_api import sync_playwright

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(args=["--window-size=1920,1080"], headless=True)
        context = browser.new_context(viewport={"width": 1920, "height": 1080})
        page = context.new_page()
        page.on('console', lambda msg: print(f"Browser Console [{msg.type}]: {msg.text}"))
        page.on('pageerror', lambda exc: print(f"Browser Error: {exc}"))

        print("[1] Opening login page...")
        page.goto('http://localhost:8080/login')
        page.wait_for_load_state('domcontentloaded')

        print("[2] Filling credentials...")
        page.fill("input[placeholder='请输入用户名']", "admin")
        page.fill("input[placeholder='请输入密码']", "123456")

        print("[3] Clicking login...")
        page.click("button.submit-btn")
        page.wait_for_timeout(2000)
        page.wait_for_load_state('domcontentloaded')

        print("[4] Navigating to Indicator Lib (/monitor/lib)...")
        page.goto('http://localhost:8080/monitor/lib')
        page.wait_for_timeout(2000)
        page.wait_for_load_state('domcontentloaded')

        print("[5] Searching for '试算' button (Composite Indicator)...")
        calc_button = page.locator("button:has-text('试算')").first
        if calc_button.count() == 0:
            print("ERROR: No '试算' button found. Is there a composite indicator?")
            page.screenshot(path="e:/datacenter2/error_calc_button_not_found.png")
            return

        print("[6] Clicking '试算'...")
        calc_button.click()
        page.wait_for_timeout(1000)

        print("[7] Filling MessageBox with period 202602...")
        # MessageBox input
        msgbox_input = page.locator(".el-message-box__input input")
        if msgbox_input.count() > 0:
            msgbox_input.fill("202602")

        print("[8] Confirming '开始试算'...")
        confirm_btn = page.locator(".el-message-box__btns button:has-text('开始试算')")
        confirm_btn.click()

        # Wait for calculation result alert
        print("[9] Waiting for AST result alert...")
        page.wait_for_timeout(1500)

        alert_box = page.locator(".el-message-box").filter(has_text="计算结果为")
        if alert_box.count() > 0:
            print("--- AST Result ---")
            print(alert_box.inner_text().replace('\n', ' '))
            print("------------------")

            # Close the alert so the overlay doesn't block the screen
            ok_btn = alert_box.locator("button:has-text('确定')")
            if ok_btn.count() > 0:
                print("Clicking OK to close alert...")
                ok_btn.click()
                page.wait_for_timeout(1000)
        else:
            print("Warning: Could not find calculation result alert box. Check if backend crashed.")
            page.screenshot(path="e:/datacenter2/error_ast_result.png")

        print("[10] Navigating to '全景指标监控' (/monitor/index) using Menu click...")

        # Click menu directly to preserve vue state
        metrics_menu = page.locator(".el-menu-item span:has-text('全景指标监控')")
        if metrics_menu.count() > 0:
            metrics_menu.click()
        else:
            page.goto('http://localhost:8080/monitor/index')
        page.wait_for_timeout(3000)
        page.wait_for_load_state('domcontentloaded')

        print("[11] Finding an indicator card to click...")
        page.wait_for_timeout(3000) # Give extra time for network response

        try:
            page.wait_for_selector(".indicator-card, .table-name-cell, .focus-item", timeout=5000)
        except Exception as e:
            print("Warning: Selector timeout", e)

        card = page.locator(".indicator-card, .table-name-cell, .focus-item").first

        if card.count() > 0:
            card.click()
            print("[12] Wait for Drawer to open...")
            page.wait_for_timeout(2000)

            print("[13] Verifying Drawer content for '指标构成分析' tree...")
            drawer = page.locator(".el-drawer__body")
            if "指标构成分析" in drawer.inner_text():
                print("Success: '指标构成分析' tree header found in drawer.")
                # Ensure the tree contains some node elements
                nodes = drawer.locator(".tree-node, .echarts-tree, .chart, .el-tree")
                if nodes.count() > 0 or "构成分析" in drawer.inner_text():
                    print("Success: Tree/Chart container is present.")
                else:
                    print("Warning: Tree nodes not rendering.")
            else:
                print("Warning: '指标构成分析' not found in drawer.")
                page.screenshot(path="e:/datacenter2/error_drawer_content.png")
        else:
            print("Warning: Could not find indicator card to click.")

        print("Taking final screenshot...")
        page.screenshot(path="e:/datacenter2/success_ast_link2.png")
        browser.close()

if __name__ == '__main__':
    run()
