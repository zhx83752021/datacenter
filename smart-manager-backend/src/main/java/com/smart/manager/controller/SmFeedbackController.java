package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.smart.manager.annotation.Log;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmFeedback;
import com.smart.manager.service.ISmFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 指标反馈管理接口
 */
@RestController
@RequestMapping("/api/sm/feedback")
@RequiredArgsConstructor
public class SmFeedbackController {

    private final ISmFeedbackService feedbackService;

    /**
     * 手工发起一条新的指标反馈/工单
     */
    @PostMapping("/create")
    // @Log(title = "指标反馈管理", businessType = "手工建单", operatorType = "后台用户",
    // isSaveRequestData = true)
    public Result<Boolean> createFeedback(@RequestBody SmFeedback feedback) {
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setUpdateTime(LocalDateTime.now());
        // 0: 待处理
        if (feedback.getStatus() == null) {
            feedback.setStatus(0);
        }
        return Result.success(feedbackService.save(feedback));
    }

    /**
     * 处理指标反馈进度 (通过审批、补充结果信息、打回等)
     */
    @PostMapping("/process")
    // @Log(title = "指标反馈管理", businessType = "工单处理", operatorType = "后台用户",
    // isSaveRequestData = true)
    public Result<Boolean> processFeedback(@RequestBody SmFeedback feedback) {
        if (feedback.getId() == null) {
            return Result.error("工单ID不能为空");
        }
        feedback.setUpdateTime(LocalDateTime.now());
        // 在这里可以接续站内信/移动端推送代码逻辑，例如：
        // noticeService.pushMessage(feedback.getCreateBy(), "您的指标反馈状态已更新");
        return Result.success(feedbackService.updateById(feedback));
    }

    /**
     * 获取工单列表
     */
    @GetMapping("/list")
    public Result<Page<SmFeedback>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long indicatorId,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SmFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(indicatorId != null, SmFeedback::getIndicatorId, indicatorId)
                .eq(status != null, SmFeedback::getStatus, status)
                .orderByDesc(SmFeedback::getCreateTime);

        Page<SmFeedback> page = new Page<>(pageNum, pageSize);
        return Result.success(feedbackService.page(page, wrapper));
    }
}
