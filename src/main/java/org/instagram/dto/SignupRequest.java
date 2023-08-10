package org.instagram.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(level = AccessLevel.PUBLIC)
public class SignupRequest {
    private String name;
    private String email;
    private String username;
    private String password;
    private String profileUrl;
    private Boolean isPrivate;

}
