package com.wmmp.file.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.common.utils.SecurityUtil;
import com.wmmp.file.entity.SysFile;
import com.wmmp.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/** 文件管理接口 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final SecurityUtil securityUtil;

    /** 文件上传 */
    @PostMapping("/upload")
    public R<SysFile> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return R.ok(fileService.upload(file, securityUtil.getCurrentUserId()));
    }

    /** 分页查询文件列表 */
    @GetMapping
    public R<PageResult<SysFile>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(PageResult.of(fileService.page(pageNum, pageSize)));
    }

    /** 文件下载 */
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        SysFile sysFile = fileService.getById(id);
        Path path = Paths.get(fileService.getUploadPath(), sysFile.getFilePath());
        if (!Files.exists(path)) { response.sendError(404, "文件不存在"); return; }
        String encodedName = URLEncoder.encode(sysFile.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
        response.setContentLengthLong(Files.size(path));
        try (OutputStream os = response.getOutputStream()) { Files.copy(path, os); }
    }

    /** 删除文件 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        fileService.delete(id); return R.ok();
    }
}
