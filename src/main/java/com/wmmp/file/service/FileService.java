package com.wmmp.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.common.exception.BizException;
import com.wmmp.file.entity.SysFile;
import com.wmmp.file.mapper.SysFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/** 文件服务：上传、下载、删除 */
@Service
@RequiredArgsConstructor
public class FileService {
    private final SysFileMapper fileMapper;
    @Value("${wdmp.file.upload-path:./uploads}")
    private String uploadPath;
    @Value("${wdmp.file.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,txt,zip}")
    private String allowedTypes;

    /** 文件上传：存储到本地，记录到数据库 */
    public SysFile upload(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) throw new BizException("上传文件不能为空");
        String ext = getExtension(file.getOriginalFilename());
        checkAllowedType(ext);
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = Paths.get(uploadPath, datePath);
        Files.createDirectories(dir);
        file.transferTo(dir.resolve(storedName));
        SysFile sysFile = new SysFile();
        sysFile.setFileName(file.getOriginalFilename());
        sysFile.setFilePath(datePath + "/" + storedName);
        sysFile.setFileSize(file.getSize());
        sysFile.setFileType(ext);
        sysFile.setUploadUser(userId);
        sysFile.setUploadTime(LocalDateTime.now());
        fileMapper.insert(sysFile);
        return sysFile;
    }

    public IPage<SysFile> page(int pageNum, int pageSize) {
        return fileMapper.selectPage(new Page<>(pageNum, pageSize), null);
    }

    public SysFile getById(Long id) {
        SysFile file = fileMapper.selectById(id);
        if (file == null) throw new BizException("文件不存在");
        return file;
    }

    public void delete(Long id) {
        SysFile file = getById(id);
        try { Files.deleteIfExists(Paths.get(uploadPath, file.getFilePath())); } catch (IOException ignored) {}
        fileMapper.deleteById(id);
    }

    public String getUploadPath() { return uploadPath; }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private void checkAllowedType(String ext) {
        boolean allowed = Arrays.stream(allowedTypes.split(",")).anyMatch(t -> t.trim().equalsIgnoreCase(ext));
        if (!allowed) throw new BizException("不支持上传 ." + ext + " 类型的文件");
    }
}
