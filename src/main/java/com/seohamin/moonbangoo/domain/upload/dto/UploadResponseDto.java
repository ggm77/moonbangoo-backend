package com.seohamin.moonbangoo.domain.upload.dto;

import lombok.Getter;

@Getter
public class UploadResponseDto {

    private final String url;

    public UploadResponseDto(final String url){
        this.url = url;
    }
}
