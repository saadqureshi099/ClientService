package org.instagram.clients;

import org.instagram.dto.AddStoryDto;
import org.instagram.dto.StoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "STORY-SERVICE", url = "http://api-gateway-service:8080/story")
public interface StoryServiceClient {
    @GetMapping("/stories/{userid}")
    public List<StoryDto> getUserStories(@PathVariable long userid);

    @PostMapping("/addStory")
    public StoryDto addStory(@RequestBody AddStoryDto storydto);
    @DeleteMapping("/{id}")
    public void deleteStory(@PathVariable long id);
}
