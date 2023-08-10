package org.instagram.dto.Feed;


import lombok.Data;

import java.util.List;

@Data
public class FeedDto {
    long userid;
    String profileUrl;
    String username;
    List<StoryFeedDto> storyFeedDtos;
    List<PostFeedDto> postFeedDtos;

}
