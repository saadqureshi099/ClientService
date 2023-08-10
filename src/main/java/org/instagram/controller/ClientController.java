package org.instagram.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instagram.clients.*;
import org.instagram.dto.*;
import org.instagram.dto.Feed.FeedDto;
import org.instagram.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClientController {

    private final UserServiceClient userServiceClient;
    private final ClientService clientService;


    private static final String HOME_REDIRECT= "redirect:/home";
    private static final String CONTENT= "content";

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginRequest());
        return "login";
    }

    /**
     * Used to log in a user, upon success stores the user id and token in the session
     * @param loginRequest
     * @param redirectAttributes
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(@Validated LoginRequest loginRequest,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            String token = userServiceClient.authenticateAndGetToken(loginRequest);

            session.setAttribute("token", token);

            long uid = clientService.getUserId(loginRequest.getUsername());

            session.setAttribute("uid", uid);

            return HOME_REDIRECT;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "failed";
        }
    }
    @GetMapping("/signup")
    public String showSignupForm(Model model){
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    @ResponseBody
    public String signupUser(HttpServletRequest request, @RequestParam("profileUrl") MultipartFile profileImg){
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName(request.getParameter(SignupRequest.Fields.name));
        signupRequest.setEmail(request.getParameter("email"));
        signupRequest.setUsername(request.getParameter("username"));
        signupRequest.setPassword(request.getParameter("password"));
        signupRequest.setIsPrivate(Boolean.valueOf(request.getParameter("isPrivate")));

        try {
            clientService.signupUser(signupRequest, profileImg);
            return "Signup successfully";
        } catch (Exception e) {
            return "Signup Unsuccessful";
        }

    }
    @PostMapping("/addpost")
    public String addPost(HttpServletRequest request,@RequestParam("imageFiles") List<MultipartFile> imageFiles, HttpSession session){

        AddPostDto addPostDto = new AddPostDto();
        addPostDto.setContent(request.getParameter(CONTENT));
        addPostDto.setUserId((long) session.getAttribute("uid"));
        addPostDto.setImages(new ArrayList<>());
        clientService.addPost(addPostDto,imageFiles);

        return HOME_REDIRECT;
    }

    @PostMapping("/editpost")
    public String editPost(HttpServletRequest request,@RequestParam("imageFiles") List<MultipartFile> imageFiles, HttpSession session){

        AddPostDto addPostDto = new AddPostDto();
        long postId = Long.parseLong(request.getParameter("postId"));
        addPostDto.setContent(request.getParameter(CONTENT));
        addPostDto.setUserId((long) session.getAttribute("uid"));
        clientService.editPost(postId, addPostDto, imageFiles);

        return HOME_REDIRECT;
    }
    @PostMapping("/editcomment")
    public String editComment(HttpServletRequest request,HttpSession session) {
        long commentId = Long.parseLong(request.getParameter("commentid"));

        PostCommentDto postCommentDto = PostCommentDto.builder()
                .content(request.getParameter(CONTENT))
                .userId((long) session.getAttribute("uid"))
                .build();

        clientService.editComment(commentId, postCommentDto);

        return HOME_REDIRECT;
    }
        @PostMapping("/addstory")
    public String addStory(@RequestParam("storyImg") MultipartFile storyImg, HttpSession session) throws IOException {
        AddStoryDto addStoryDto = new AddStoryDto();
        addStoryDto.setUserId((long) session.getAttribute("uid"));
        clientService.addStory(addStoryDto, storyImg);

        return HOME_REDIRECT;
    }
    @PostMapping("/addcomment")
    public String addComment(@ModelAttribute("addComment") PostCommentDto addComment, @RequestParam("userid") long userid,
                             @RequestParam("postid") long postid){

        clientService.addComment(addComment,userid, postid);

        return HOME_REDIRECT;
    }
    @GetMapping("/home")
    public String getHomeFeed( Model model, HttpSession session){

        if(session.getAttribute("token")==null)
            return "redirect:/login";

        long uid = (long) session.getAttribute("uid");
        model.addAttribute("userRequests",clientService.getUserRequests(uid));
        model.addAttribute("followed",clientService.getUsersFollowed(uid));
        model.addAttribute("feedDto",  clientService.generateFeed(uid));
        model.addAttribute("userList",clientService.getAllUsersList());
        model.addAttribute("addComment", new PostCommentDto());
        model.addAttribute("addPostDto", new AddPostDto());
        model.addAttribute("addStoryDto", new AddStoryDto());

        return "index";

    }

    @GetMapping("/getfeeddto")
    @ResponseBody
    public FeedDto getfeeddto( Model model, HttpSession session){
        long uid = (long) session.getAttribute("uid");
        FeedDto feedDto = clientService.generateFeed(uid);
        model.addAttribute(feedDto);
        model.addAttribute("addComment", new PostCommentDto());
        model.addAttribute("addPostDto", new AddPostDto());
        model.addAttribute("addStoryDto", new AddStoryDto());

        return feedDto;

    }
    @PostMapping("/deletepost")
    public String deletePost(HttpServletRequest request){
        long postId = Long.parseLong(request.getParameter("postId"));
        clientService.deletePost(postId);
        return HOME_REDIRECT;
    }
    @PostMapping("/deletestory")
    public String deleteStory(HttpServletRequest request){
        long storyId = Long.parseLong(request.getParameter("storyId"));

       clientService.deleteStory(storyId);
        return HOME_REDIRECT;
    }
    @PostMapping("/deletecomment")
    public String deleteComment(HttpServletRequest request){
        long commentId = Long.parseLong(request.getParameter("commentid"));
        clientService.deleteComment(commentId);
        return HOME_REDIRECT;
    }

    @PostMapping("/followuser")
    @ResponseBody
    public String followUser(@RequestBody Map<String, Long> request, HttpSession session) {
        long uid = (long) session.getAttribute("uid");
        long followedUserId = request.get("userId");

        clientService.followUser(uid,followedUserId);
        return "Success";
    }
    @PostMapping("/unfollowuser")
    @ResponseBody
    public String unfollowUser(@RequestBody Map<String, Long> request, HttpSession session) {
        long uid = (long) session.getAttribute("uid");
        long followedUserId = request.get("userId");
        clientService.unFollowUser(uid,followedUserId);
        return "Success";
    }
    @PostMapping("/acceptrequest")
    public String acceptRequest(HttpServletRequest request, HttpSession session) {
        long uid = (long) session.getAttribute("uid");
        long userRequestId = Long.parseLong(request.getParameter("userid"));
        clientService.acceptRequest(uid,userRequestId);

        return HOME_REDIRECT;
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
            // Invalidate the session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return "redirect:/login";
        }

    @PostMapping("/likepost")
    public String likePost(@RequestParam("postId") long likedPostId, HttpSession session) {
        long uid = (Long) session.getAttribute("uid");
        clientService.likePost(uid,likedPostId);
        return HOME_REDIRECT;
    }

}
