package org.instagram.dto;

import lombok.Data;


@Data
public class UserDto {
    private long id;
    private String name;
    private String username;
    private String profileUrl;
    private Boolean isPrivate;
}
