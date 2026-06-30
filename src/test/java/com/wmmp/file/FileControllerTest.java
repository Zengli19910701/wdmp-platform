package com.wmmp.file;

import com.wmmp.common.exception.BizException;
import com.wmmp.common.exception.GlobalExceptionHandler;
import com.wmmp.file.controller.FileController;
import com.wmmp.file.entity.SysFile;
import com.wmmp.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FileController 接口测试（MockMvc）
 * 覆盖：文件上传成功/类型不允许、文件列表、删除
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileController 接口测试")
class FileControllerTest {
    private MockMvc mockMvc;
    @Mock private FileService fileService;
    @InjectMocks private FileController fileController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("POST /api/files/upload - 合法文件上传成功返回 200")
    void upload_validFile_should200() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        SysFile sysFile = new SysFile();
        sysFile.setId(1L); sysFile.setFileName("test.txt"); sysFile.setFileType("txt"); sysFile.setFileSize(7L);
        given(fileService.upload(any(), anyLong())).willReturn(sysFile);
        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.txt"));
    }

    @Test
    @DisplayName("POST /api/files/upload - 禁止类型（.exe）返回业务错误")
    void upload_forbiddenType_shouldReturnBizError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bad.exe", "application/octet-stream", "bad".getBytes());
        given(fileService.upload(any(), anyLong())).willThrow(new BizException("不支持上传 .exe 类型的文件"));
        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("不支持上传 .exe 类型的文件"));
    }

    @Test
    @DisplayName("DELETE /api/files/{id} - 文件不存在返回业务错误")
    void delete_notFound_shouldReturnBizError() throws Exception {
        willThrow(new BizException("文件不存在")).given(fileService).delete(99L);
        mockMvc.perform(delete("/api/files/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("文件不存在"));
    }

    @Test
    @DisplayName("DELETE /api/files/{id} - 正常删除返回 200")
    void delete_success_should200() throws Exception {
        willDoNothing().given(fileService).delete(1L);
        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
