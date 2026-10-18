package com.seohamin.moonbangoo.domain.upload;

import com.jayway.jsonpath.JsonPath;
import com.seohamin.moonbangoo.domain.admin.AdminLoggedInConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AdminLoggedInConfig.class)
@ActiveProfiles("test")
class UploadAdminFlowTest {

    //PNG 시그니처 뒤에 아무 바이트나 붙인 가짜 이미지 (형식 판별은 앞부분만 봄)
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 1, 2, 3, 4};

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file(final String name, final byte[] bytes){
        return new MockMultipartFile("file", name, "application/octet-stream", bytes);
    }

    @Test
    void 이미지를_올리면_주소가_돌아오고_그_주소로_다시_볼_수_있다() throws Exception {
        final String response = mockMvc.perform(multipart("/api/v1/admin/upload").file(file("bakery.png", PNG)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(startsWith("http://localhost/uploads/")))
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.endsWith(".png")))
                .andReturn().getResponse().getContentAsString();

        final String path = URI.create(JsonPath.read(response, "$.url")).getPath();

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(PNG));
    }

    @Test
    void 파일_이름이_아니라_내용으로_형식을_판별한다() throws Exception {
        //이름은 png지만 내용이 HTML인 파일
        mockMvc.perform(multipart("/api/v1/admin/upload")
                        .file(file("evil.png", "<html><script>alert(1)</script></html>".getBytes())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_IMAGE"));

        //SVG는 스크립트를 넣을 수 있어서 허용하지 않음
        mockMvc.perform(multipart("/api/v1/admin/upload")
                        .file(file("a.svg", "<svg xmlns=\"http://www.w3.org/2000/svg\"/>".getBytes())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_IMAGE"));

        //이름이 없거나 이상해도 내용이 이미지면 저장되고, 저장 이름은 서버가 정함
        mockMvc.perform(multipart("/api/v1/admin/upload").file(file("../../x.exe", PNG)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(startsWith("http://localhost/uploads/")))
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(".."))));
    }

    @Test
    void 빈_파일이나_파일이_없는_요청은_거절한다() throws Exception {
        mockMvc.perform(multipart("/api/v1/admin/upload").file(file("empty.png", new byte[0])))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_IMAGE"));
    }

    @Test
    void 업로드_폴더_밖의_파일은_볼_수_없다() throws Exception {
        mockMvc.perform(get("/uploads/../application.yaml"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/uploads/%2e%2e/application.yaml"))
                .andExpect(status().isNotFound());
    }
}
