package org.instagram.dto.Feed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoryFeedDto {
    long id;
    String username;
    String profileUrl;
    String imgUrl;
}
