package org.instagram.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instagram.clients.FollowServiceClient;
import org.instagram.clients.PostServiceClient;
import org.instagram.clients.StoryServiceClient;
import org.instagram.clients.UserServiceClient;
import org.instagram.dto.*;
import org.instagram.dto.Feed.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {

    private final UserServiceClient userServiceClient;
    private final StoryServiceClient storyServiceClient;
    private final FollowServiceClient followServiceClient;
    private final PostServiceClient postServiceClient;
    private final Cloudinary cloudinary;
    private static final String FOLDER= "folder";
    private static final String PUBLIC_ID= "public_id";
    private static final String SECURE_URL= "secure_url";
    public List<FollowedDto> getFollowed(long uid){
        return followServiceClient.getFollowed(uid);
    }



    public List<StoryDto> getUserStories(long followedId) {
        return storyServiceClient.getUserStories(followedId);
    }
    public  long getUserId(String username){
        return userServiceClient.getUserId(username);
    }


    public  List<PostDto> getUserPosts(long followedId) {
        return postServiceClient.getPostsByUserId(followedId);
    }

    /**
     * Generates Feed for a User
     * Add Posts with their images,comments and likes
     * Add Stories
     * also add User information with each story,post and comment
     * @param uid
     * @return
     */
    public FeedDto generateFeed(long uid) {
        FeedDto feedDto = new FeedDto();

        UserDto currentUser = requireNonNull(userServiceClient.findByUserId(uid).getBody());
        feedDto.setProfileUrl(currentUser.getProfileUrl());
        feedDto.setUsername(currentUser.getUsername());
        feedDto.setUserid(currentUser.getId());

        /**
         * adding self to the followed list so the feed can show user own posts and stories
         */
        FollowedDto self = new FollowedDto();
        self.setFollowedId(uid);
        self.setId(500);
        self.setUserId(uid);
        List<FollowedDto> followed = this.getFollowed(uid);
        followed.add(self);

        /**
         * Getting the stories of users followed
         */
        List<StoryFeedDto> storyFeedDtos = followed.stream()
                .map(followedUser -> userServiceClient.findByUserId(followedUser.getFollowedId()).getBody())
                .flatMap(userdetails -> getUserStories(userdetails.getId()).stream()
                        .map(userStory -> StoryFeedDto.builder()
                                .id(userStory.getId())
                                .imgUrl(userStory.getStoryImg())
                                .username(userdetails.getUsername())
                                .profileUrl(userdetails.getProfileUrl())
                                .build()
                        )
                )
                .toList();
        /**
         * getting the posts of users followed
         */
        List<PostFeedDto> postFeedDtos = followed.stream()
                .map(followedUser -> userServiceClient.findByUserId(followedUser.getFollowedId()).getBody())
                .flatMap(userdetails -> getUserPosts(userdetails.getId()).stream()
                        .map(userPost -> {
                            List<PostFeedImageDto> postFeedImageDtoList = userPost.getImages().stream()
                                    .map(postImageDto -> {
                                        PostFeedImageDto postFeedImageDto = new PostFeedImageDto();
                                        postFeedImageDto.setImageUrl(postImageDto.getImageUrl());
                                        return postFeedImageDto;
                                    })
                                    .toList();

                            /**
                             * getting the comments on the post
                             */
                            List<PostFeedCommentDto> postFeedCommentDtos = userPost.getComments().stream()
                                    .map(postCommentDto -> {
                                        UserDto commentUser = userServiceClient.findByUserId(postCommentDto.getUserId()).getBody();
                                        PostFeedCommentDto postFeedCommentDto = new PostFeedCommentDto();
                                        postFeedCommentDto.setUsername(commentUser.getUsername());
                                        postFeedCommentDto.setId(postCommentDto.getId());
                                        postFeedCommentDto.setContent(postCommentDto.getContent());
                                        postFeedCommentDto.setUserId(commentUser.getId());
                                        return postFeedCommentDto;
                                    })
                                    .toList();

                            return PostFeedDto.builder()
                                    .id(userPost.getId())
                                    .username(userdetails.getUsername())
                                    .content(userPost.getContent())
                                    .profileUrl(userdetails.getProfileUrl())
                                    .postImages(postFeedImageDtoList)
                                    .postFeedComments(postFeedCommentDtos)
                                    .likes(postServiceClient.getLikes(userPost.getId()))
                                    .build();
                        })
                )
                .toList();

        feedDto.setPostFeedDtos(postFeedDtos);
        feedDto.setStoryFeedDtos(storyFeedDtos);

        return feedDto;
    }



    public void signupUser( SignupRequest signupRequest,MultipartFile profileImg) throws IOException {
        String profileImgUrl = null;

        if (profileImg != null && !profileImg.isEmpty()) {
            String fileName = StringUtils.cleanPath(profileImg.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(fileName);
            fileName = UUID.randomUUID().toString() + "." + extension;


            Map<String,Object> uploadResult = cloudinary.uploader().upload(profileImg.getBytes(), ObjectUtils.asMap(
                    FOLDER, "instagram/profileImg",
                    PUBLIC_ID, fileName
            ));
            profileImgUrl = (String) uploadResult.get(SECURE_URL);
        }

        signupRequest.setProfileUrl(profileImgUrl);
        userServiceClient.addUser(signupRequest);
    }

    /**
     * Upload each image to cloud and added to post.
     * Post is then saved
     * @param addPostDto
     * @param imageFiles
     */
    public void addPost(AddPostDto addPostDto, List<MultipartFile> imageFiles) {
        for (MultipartFile imageFile : imageFiles) {
            /**
             * Handle each image file
             */
            if (!imageFile.isEmpty()) {
                try {
                    String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                    /**
                     * Upload the image file to Cloudinary
                     */
                    Map<String,Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                            FOLDER, "instagram/posts",
                            PUBLIC_ID, fileName
                    ));

                    /**
                     * Extract the URL of the uploaded image from the upload result
                     */
                    String imageUrl = (String) uploadResult.get(SECURE_URL);

                    /**
                     * Create a PostImageDto object and set the image URL
                      */
                    AddImageDto imageDto = AddImageDto.builder().build();
                    imageDto.setImageUrl(imageUrl);

                    /**
                     * Add the imageDto to the addPostDto's images list
                     */

                    addPostDto.getImages().add(imageDto);

                } catch (IOException e) {
                    /**
                     * Handle any errors that occur during the upload
                     */
                    e.printStackTrace();
                }
            }
        }
        postServiceClient.addPost(addPostDto);
    }

    /**
     * Edit Post Method
     * Update images and Content
     * Saves Post
     * @param postId
     * @param addPostDto
     * @param imageFiles
     */
    public void editPost(long postId,AddPostDto addPostDto, List<MultipartFile> imageFiles) {
        addPostDto.setImages(new ArrayList<>());
        for (MultipartFile imageFile : imageFiles) {
            // Handle each image file as needed
            if (!imageFile.isEmpty()) {
                try {
                    String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                    // Upload the image file to Cloudinary
                    Map<String,Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                            FOLDER, "instagram/posts",
                           PUBLIC_ID, fileName
                    ));

                    // Extract the URL of the uploaded image from the upload result
                    String imageUrl = (String) uploadResult.get(SECURE_URL);

                    // Create a PostImageDto object and set the image URL
                    AddImageDto imageDto = AddImageDto.builder().build();
                    imageDto.setImageUrl(imageUrl);

                    // Add the imageDto to the addPostDto's images list
                    addPostDto.getImages().add(imageDto);

                } catch (IOException e) {
                    // Handle any errors that occur during the upload
                    e.printStackTrace();
                }
            }
        }
        postServiceClient.updatePost(postId,addPostDto);
    }

    public void editComment(long commentId, PostCommentDto postCommentDto) {
        postServiceClient.updateComment(commentId,postCommentDto);
    }

    public void addStory(AddStoryDto addStoryDto, MultipartFile storyImg) throws IOException {

        String storyImgUrl = null;
        if (storyImg != null && !storyImg.isEmpty()) {
            String fileName = UUID.randomUUID().toString();


            Map<String,Object> uploadResult = cloudinary.uploader().upload(storyImg.getBytes(), ObjectUtils.asMap(
                    FOLDER, "instagram/stories",
                    PUBLIC_ID, fileName
            ));
            storyImgUrl = (String) uploadResult.get(SECURE_URL);
        }
        addStoryDto.setStoryImg(storyImgUrl);
        addStoryDto.setDate(new Date());
        storyServiceClient.addStory(addStoryDto);
    }

    public void addComment(PostCommentDto addComment, long userid, long postid) {
        PostCommentDto comment = new PostCommentDto();
        comment.setPostId(postid);
        comment.setUserId(userid);
        comment.setContent(addComment.getContent());
        postServiceClient.addComment(comment);
    }

    public void deletePost(long postId) {
        postServiceClient.deletePost(postId);
    }

    public void deleteStory(long storyId) {
        storyServiceClient.deleteStory(storyId);
    }

    public void deleteComment(long commentId) {
        postServiceClient.deleteComment(commentId);
    }

    public void followUser(long uid, long followedUserId) {
        FollowDto followDto = FollowDto.builder()
                .userId(uid)
                .followedId(followedUserId)
                .build();
        followServiceClient.addFollow(followDto);
    }

    public void unFollowUser(long uid, long followedUserId) {
        FollowDto followDto = FollowDto.builder()
                .userId(uid)
                .followedId(followedUserId)
                .build();
        followServiceClient.unFollow(followDto);
    }

    public void acceptRequest(long uid, long userRequestId) {
        FollowDto followDto = FollowDto.builder()
                .userId(userRequestId)
                .followedId(uid)
                .build();
        followServiceClient.acceptRequest(followDto);
    }

    public void likePost(long uid, long likedPostId) {
        postServiceClient.likePost(uid,likedPostId);
    }

    public List<UserDto> getAllUsersList() {
        return userServiceClient.findAll().getBody();
    }

    public List<Long> getUsersFollowed(long uid) {
        List<FollowedDto> followed= followServiceClient.getFollowed(uid);
        List<Long> usersFollowed = new ArrayList<>();

        usersFollowed.addAll(
                followed.stream()
                        .map(FollowedDto::getFollowedId)
                        .toList()
        );
        return usersFollowed;
    }

    public List<UserDto> getUserRequests(long uid) {
        List<FollowedDto> requests = followServiceClient.getRequests(uid);

        return requests.stream()
                .map(f -> userServiceClient.findByUserId(f.getUserId()).getBody())
                .toList();
    }
}
