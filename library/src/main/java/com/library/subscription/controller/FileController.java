package com.library.subscription.controller;

import com.library.subscription.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class FileController {

    // 上传文件保存路径
    private static final String UPLOAD_DIR = "upload";

    /**
     * 处理文件上传请求
     * 
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping
    public R<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传的文件为空");
        }

        try {
            // 确保上传目录存在
            String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(uploadPath, newFilename);
            Files.write(filePath, file.getBytes());

            // 返回可访问的URL
            String fileUrl = "/api/upload/files/" + newFilename;

            return R.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取上传的文件
     * 
     * @param filename 文件名
     * @return 文件资源
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // 尝试确定文件的内容类型
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException e) {
                    // 忽略错误
                }

                // 如果无法确定内容类型，则使用通用二进制类型
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}