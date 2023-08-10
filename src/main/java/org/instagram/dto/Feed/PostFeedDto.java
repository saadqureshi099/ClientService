package org.instagram.dto.Feed;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PostFeedDto {
    long id;
    String username;
    String profileUrl;
    String content;
    List<PostFeedImageDto> postImages;
    List<PostFeedCommentDto> postFeedComments;
    long likes;



}
