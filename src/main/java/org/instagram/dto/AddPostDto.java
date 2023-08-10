package org.instagram.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddPostDto {
        private long userId;
        private String content;
        private List<AddImageDto> images;
}
