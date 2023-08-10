package org.instagram.dto.Feed;

import lombok.Data;

@Data
public class PostFeedCommentDto {
    long id;
    long userId;
    String username;
    String content;
}
