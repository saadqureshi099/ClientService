package org.instagram.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowDto {
    private long userId;
    private long followedId;
}
