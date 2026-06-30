package com.wmmp.file;

import com.wmmp.common.exception.BizException;
import com.wmmp.file.entity.SysFile;
import com.wmmp.file.mapper.SysFileMapper;
import com.wmmp.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * FileService 单元测试
 * 覆盖：不允许的文件类型、正常上传、空文件、删除不存在的文件记录
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileService 单元测试")
class FileServiceTest {
    @Mock private SysFileMapper fileMapper;
    @InjectMocks private FileService fileService;
    @TempDir Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileService, "allowedTypes", "jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar,txt");
    }

    @Test
    @DisplayName("上传：不允许的文件类型（.exe）抛出 BizException")
    void upload_forbiddenType_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile("file", "virus.exe", "application/octet-stream", "content".getBytes());
        assertThatThrownBy(() -> fileService.upload(file, 1L))
                .isInstanceOf(BizException.class).hasMessageContaining("不支持上传");
    }

    @Test
    @DisplayName("上传：空文件抛出 BizException")
    void upload_emptyFile_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        assertThatThrownBy(() -> fileService.upload(file, 1L))
                .isInstanceOf(BizException.class).hasMessageContaining("上传文件不能为空");
    }

    @Test
    @DisplayName("上传：合法 txt 文件正常保存")
    void upload_validTxt_shouldSaveAndInsert() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello world".getBytes());
        given(fileMapper.insert(any(SysFile.class))).willReturn(1);
        SysFile result = fileService.upload(file, 1L);
        assertThat(result.getFileName()).isEqualTo("test.txt");
        assertThat(result.getFileType()).isEqualToIgnoringCase("txt");
        assertThat(result.getFileSize()).isEqualTo(11L);
        then(fileMapper).should().insert(any(SysFile.class));
    }

    @Test
    @DisplayName("delete：文件记录不存在时抛出 BizException")
    void delete_notFound_shouldThrow() {
        given(fileMapper.selectById(999L)).willReturn(null);
        assertThatThrownBy(() -> fileService.delete(999L))
                .isInstanceOf(BizException.class).hasMessageContaining("文件不存在");
    }

    @Test
    @DisplayName("delete：正常删除不抛异常")
    void delete_fileRecordExists_physicalMissing_shouldNotThrow() {
        SysFile sysFile = new SysFile();
        sysFile.setId(1L); sysFile.setFilePath("2024/01/01/nonexistent.txt");
        given(fileMapper.selectById(1L)).willReturn(sysFile);
        given(fileMapper.deleteById(1L)).willReturn(1);
        assertThatNoException().isThrownBy(() -> fileService.delete(1L));
        then(fileMapper).should().deleteById(1L);
    }
}
