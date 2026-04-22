package com.smart.manager.controller;

import com.smart.manager.common.Result;
import com.smart.manager.entity.SmFile;
import com.smart.manager.service.ISmFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class SmFileController {

    private final ISmFileService fileService;

    // 默认上传路径在项目根目录下的 uploads 文件夹，可通过配置文件覆盖
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    /**
     * 文件上传接口
     */
    @PostMapping("/upload")
    public Result<SmFile> upload(@RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String module) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        try {
            // 确保目录存在
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + suffix;

            // 保存文件到本地
            File dest = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);

            // 保存记录到数据库
            SmFile smFile = new SmFile();
            smFile.setFileName(originalFilename);
            smFile.setFilePath("/uploads/" + newFilename);
            smFile.setFileSize(file.getSize());
            smFile.setFileType(file.getContentType());
            smFile.setModule(module);
            smFile.setCreateTime(LocalDateTime.now());
            smFile.setUpdateTime(LocalDateTime.now());

            fileService.save(smFile);

            return Result.success(smFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取文件信息
     */
    @GetMapping("/{id}")
    public Result<SmFile> getFileInfo(@PathVariable Long id) {
        return Result.success(fileService.getById(id));
    }
}
