package org.instagram.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostDto {
    private long id;
    private long userId;
    private String content;
    private List<PostImageDto> images;
    private List<PostCommentDto> comments;
}
