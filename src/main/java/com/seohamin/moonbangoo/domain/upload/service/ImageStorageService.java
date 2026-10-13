package com.seohamin.moonbangoo.domain.upload.service;

import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

/**
 * 업로드된 이미지를 서버 디스크에 저장하는 서비스
 * 파일 이름과 Content-Type은 믿지 않고 파일 내용(앞부분 바이트)으로 이미지 형식을 판별함
 */
@Service
public class ImageStorageService {

    //파일 앞부분 12바이트면 지원하는 형식을 모두 판별할 수 있음
    private static final int HEADER_SIZE = 12;

    private final Path uploadDir;

    public ImageStorageService(@Value("${upload.dir}") final String uploadDir){
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * 이미지를 저장하는 메서드
     * @param file 업로드된 파일 (png, jpg, gif, webp만 가능)
     * @return 저장된 파일 이름 (예: 3f2a....png)
     */
    public String store(final MultipartFile file){

        if(file == null || file.isEmpty()){
            throw new CustomException(ExceptionCode.INVALID_IMAGE);
        }

        final String extension;
        try(final InputStream in = file.getInputStream()){
            extension = detectExtension(in.readNBytes(HEADER_SIZE));
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, e);
        }

        final String filename = UUID.randomUUID() + "." + extension;

        try {
            Files.createDirectories(uploadDir);
            try(final InputStream in = file.getInputStream()){
                Files.copy(in, uploadDir.resolve(filename));
            }
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, e);
        }

        return filename;
    }

    //앞부분 바이트로 이미지 형식 판별, 지원하지 않는 형식(SVG 포함)이면 예외
    private String detectExtension(final byte[] header){

        if(startsWith(header, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)){
            return "png";
        }

        if(startsWith(header, 0xFF, 0xD8, 0xFF)){
            return "jpg";
        }

        if(startsWith(header, 'G', 'I', 'F', '8', '7', 'a') || startsWith(header, 'G', 'I', 'F', '8', '9', 'a')){
            return "gif";
        }

        //WebP는 RIFF....WEBP 구조
        if(header.length >= 12
                && new String(header, 0, 4, StandardCharsets.US_ASCII).equals("RIFF")
                && new String(header, 8, 4, StandardCharsets.US_ASCII).equals("WEBP")){
            return "webp";
        }

        throw new CustomException(ExceptionCode.INVALID_IMAGE);
    }

    private boolean startsWith(final byte[] header, final int... expected){
        if(header.length < expected.length){
            return false;
        }

        final byte[] prefix = Arrays.copyOf(header, expected.length);
        for(int i = 0; i < expected.length; i++){
            if((prefix[i] & 0xFF) != expected[i]){
                return false;
            }
        }

        return true;
    }
}
