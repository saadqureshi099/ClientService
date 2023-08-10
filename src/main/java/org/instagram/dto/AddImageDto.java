package org.instagram.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddImageDto {
    String imageUrl;
}
