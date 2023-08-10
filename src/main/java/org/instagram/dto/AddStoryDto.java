package org.instagram.dto;

import lombok.Data;

import java.util.Date;
@Data
public class AddStoryDto {
    private Date date;
    private long userId;
    private String storyImg;

}
