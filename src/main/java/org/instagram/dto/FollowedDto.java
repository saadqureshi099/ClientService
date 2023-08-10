package org.instagram.dto;

import lombok.Data;

@Data
public class FollowedDto {
    long id;
    long userId;
    long followedId;
}
