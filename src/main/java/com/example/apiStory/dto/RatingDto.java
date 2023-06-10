package com.example.apiStory.dto;

import com.example.apiStory.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RatingDto {
    Long  bookId;
    Double star;
}
