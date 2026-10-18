package com.seohamin.moonbangoo.domain.upload.controller;

import com.seohamin.moonbangoo.domain.upload.dto.UploadResponseDto;
import com.seohamin.moonbangoo.domain.upload.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 사장님이 이미지를 올리는 API
 * 로그인(AdminAuthController)한 사람만 호출할 수 있음
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UploadAdminController {

    private final ImageStorageService imageStorageService;

    //이미지 업로드 API, 돌려준 주소를 팩의 image에 넣어서 사용
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> upload(
            @RequestPart("file") final MultipartFile file
    ){
        final String filename = imageStorageService.store(file);

        //프론트가 다른 주소에 있어도 쓸 수 있도록 서버 주소를 포함한 전체 주소로 돌려줌
        final String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/{filename}")
                .buildAndExpand(filename)
                .toUriString();

        return ResponseEntity.ok(new UploadResponseDto(url));
    }
}
