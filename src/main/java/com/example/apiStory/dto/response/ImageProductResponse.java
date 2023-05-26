package com.example.apiStory.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageProductResponse {
    private Long id;
    private String imageUrl;
}
