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


    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<Optional<UserEntity>> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));    }
    @GetMapping("/username/{userName}")
    public ResponseEntity<Optional<UserEntity>> getUserByUsername(@PathVariable String userName){
        return ResponseEntity.ok(userService.getUserByUserName(userName));    }

    @PostMapping("/")
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