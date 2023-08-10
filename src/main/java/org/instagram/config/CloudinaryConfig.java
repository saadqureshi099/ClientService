package org.instagram.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dyamorggu",
                "api_key", "433622789667571",
                "api_secret", "ch2tya9tjZWCV7o0GMKWpkC7d5U"));
    }
}