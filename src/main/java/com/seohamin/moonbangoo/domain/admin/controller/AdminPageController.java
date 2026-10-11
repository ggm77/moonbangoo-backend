package com.seohamin.moonbangoo.domain.admin.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 사장님이 쓰는 어드민 페이지
 * 사장님 기기 브라우저에서 http://서버주소:포트/admin 으로 접속
 * 화면과 API가 같은 서버에서 나가므로 주소가 하나이고 CORS 설정이 필요 없음
 * 인증 없음, 가게 내부망에서만 접속하는 것을 전제로 함
 */
@Hidden
@RestController
public class AdminPageController {

    private static final Resource PAGE = new ClassPathResource("admin/index.html");

    //어드민 페이지, 화면을 고쳤을 때 바로 반영되도록 캐시하지 않음
    @GetMapping(value = {"/admin", "/admin/"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> page() throws IOException {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(PAGE.getContentAsString(StandardCharsets.UTF_8));
    }
}
