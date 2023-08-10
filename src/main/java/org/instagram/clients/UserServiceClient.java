package org.instagram.clients;

import org.instagram.dto.LoginRequest;
import org.instagram.dto.SignupRequest;
import org.instagram.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service" , url = "http://api-gateway-service:8080/auth")
public interface UserServiceClient {
    @PostMapping("/register")
    void addUser(@RequestBody SignupRequest signupRequest);
    @PostMapping("/token")
    public String authenticateAndGetToken(@RequestBody LoginRequest loginRequest);
    @GetMapping("/getid/{username}")
    public long getUserId(@PathVariable String username);
    @GetMapping(value = "/user/{userid}")
    public ResponseEntity<UserDto> findByUserId(@PathVariable long userid);
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> findAll();
}

