package org.instagram.clients;

import org.instagram.dto.AddPostDto;

import org.instagram.dto.PostCommentDto;
import org.instagram.dto.PostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "POST-SERVICE", url = "http://api-gateway-service:8080/posts")
public interface PostServiceClient {
    @GetMapping("user/{userid}")
    public List<PostDto> getPostsByUserId(@PathVariable long userid);
    @PostMapping("/")
    public PostDto addPost(@RequestBody AddPostDto addPostdto);

    @PostMapping("/addComment")
    public String addComment(@RequestBody PostCommentDto commentDto);

    @PutMapping("/{id}")
    public PostDto updatePost(@PathVariable long id, @RequestBody AddPostDto post);
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id);
    @PutMapping("/comment/{id}")
    public void updateComment(@PathVariable long id,@RequestBody PostCommentDto postCommentDto);
    @DeleteMapping("/comment/{id}")
    public void deleteComment(@PathVariable long id);
    @PostMapping("/likepost")
    void likePost(@RequestParam("uid") long uid, @RequestParam("postId") long likedPostId);

    @GetMapping("getLikes/{id}")
    public long getLikes(@PathVariable long id);
}
