package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmAlertMessage;
import com.smart.manager.service.ISmAlertMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alert/message")
@RequiredArgsConstructor
public class SmAlertMessageController {

    private final ISmAlertMessageService messageService;

    /**
     * 获取预警消息列表
     */
    @PreAuthorize("hasAuthority('sm:alert:msg:list')")
    @GetMapping("/list")
    public Result<IPage<SmAlertMessage>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Integer status,
            Long indicatorId) {
        LambdaQueryWrapper<SmAlertMessage> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(SmAlertMessage::getStatus, status);
        }
        if (indicatorId != null) {
            wrapper.eq(SmAlertMessage::getIndicatorId, indicatorId);
        }
        wrapper.orderByDesc(SmAlertMessage::getCreateTime);
        return Result.success(messageService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 标记已读
     */
    @PreAuthorize("hasAuthority('sm:alert:msg:read')")
    @PutMapping("/read/{id}")
    public Result<Boolean> read(@PathVariable Long id) {
        SmAlertMessage msg = new SmAlertMessage();
        msg.setId(id);
        msg.setStatus(1); // 1:已读
        return Result.success(messageService.updateById(msg));
    }

    /**
     * 手动发送预警 (创建消息)
     */
    @PreAuthorize("hasAuthority('sm:alert:msg:add')")
    @PostMapping("/send")
    public Result<Boolean> send(@RequestBody SmAlertMessage message) {
        if (message.getCreateTime() == null) {
            message.setCreateTime(new java.util.Date());
        }
        if (message.getStatus() == null) {
            message.setStatus(0); // 未读
        }
        return Result.success(messageService.save(message));
    }
}
