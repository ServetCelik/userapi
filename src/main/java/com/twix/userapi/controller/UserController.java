package com.twix.userapi.controller;

import com.twix.userapi.business.UserService;
import com.twix.userapi.business.exceptions.InvalidCredentialsException;
import com.twix.userapi.business.exceptions.UserNotExistException;
import com.twix.userapi.controller.dto.LoginRequest;
import com.twix.userapi.repository.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
//@CrossOrigin("http://localhost:3000")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;


    @GetMapping("/test")
    public ResponseEntity<String> getTestString() {

        return ResponseEntity.ok("97");
    }
    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        Optional<UserEntity> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String userName){
        Optional<UserEntity> userOptional = userService.getUserByUserName(userName);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Long> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(createUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<String> token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return token
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed"));
        } catch (UserNotExistException | InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteUser(@PathVariable Long id) {
        return  ResponseEntity.status(HttpStatus.OK)
                .body(userService.deleteUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable("id") long id
            ,@RequestBody UserEntity userEntity) {

        return  ResponseEntity.status(HttpStatus.OK).
                body(userService.updateUser(id,userEntity));
    }

    @PostMapping("/{userId}/follow/{followId}")
    public ResponseEntity<Void> followUser(@PathVariable Long userId, @PathVariable Long followId) {
        userService.followUser(userId, followId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{userId}/unfollow/{unfollowId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId, @PathVariable Long unfollowId) {
        userService.unfollowUser(userId, unfollowId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}





//        rabbitTemplate.convertAndSend("userUpdated", "#", null, m -> {
//            m.getMessageProperties().getHeaders().put("MessageType", "CreateTweetEvent");
//            return m;
//        });
//return ResponseEntity.ok(userService.GetAllUsers());