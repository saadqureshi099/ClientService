package org.instagram.clients;


import org.instagram.dto.FollowDto;
import org.instagram.dto.FollowedDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "FOLLOW-SERVICE", url = "http://api-gateway-service:8080/follow")
public interface FollowServiceClient {

    @GetMapping("/followed/{userid}")
    public List<FollowedDto> getFollowed(@PathVariable long userid);
    @PostMapping("/addFollow")
    public void addFollow(@RequestBody FollowDto followDto);
    @PostMapping("/unFollow")
    public void unFollow(@RequestBody FollowDto followDto);
    @GetMapping("/requests/{userid}")
    public List<FollowedDto> getRequests(@PathVariable long userid);
    @PostMapping("/acceptRequest")
    public void acceptRequest(@RequestBody FollowDto followDto);
}
