package com.twix.userapi.controller;

import com.twix.userapi.business.TokenService;
import com.twix.userapi.business.UserService;
import com.twix.userapi.business.exceptions.InvalidCredentialsException;
import com.twix.userapi.business.exceptions.UserNotExistException;
import com.twix.userapi.controller.dto.LoginRequest;
import com.twix.userapi.controller.dto.UserDTO;
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
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;


    @GetMapping("/test")
    public ResponseEntity<String> getTestString() {

        return ResponseEntity.ok("97");
    }
    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String token){
        if (tokenService.isAuthorized(token,id)){
            try {
                UserDTO userDTO = userService.getUserById(id);
                return ResponseEntity.ok(userDTO);
            } catch (UserNotExistException e) {
                return ResponseEntity.notFound().build();
            }
        }else{
            return ResponseEntity.status(403).body("Forbidden");
        }
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String userName, @RequestHeader("Authorization") String token){
        if (tokenService.isAuthorized(token,userName)){
            try {
                UserDTO userDTO = userService.getUserByUserName(userName);
                return ResponseEntity.ok(userDTO);
            } catch (UserNotExistException e) {
                return ResponseEntity.notFound().build();
            }
        }else{
            return ResponseEntity.status(403).body("Forbidden");
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
    public ResponseEntity<Long> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (tokenService.isAuthorized(token,id)){
        }else{
            return ResponseEntity.status(403).body(id);
        }
        return  ResponseEntity.status(HttpStatus.OK)
                .body(userService.deleteUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable("id") long id
            ,@RequestBody UserEntity userEntity, @RequestHeader("Authorization") String token) {
        if (tokenService.isAuthorized(token,id)){
        }else{
            return ResponseEntity.status(403).body(id);
        }

        return  ResponseEntity.status(HttpStatus.OK).
                body(userService.updateUser(id,userEntity));
    }

    @PostMapping("/{userId}/follow/{followId}")
    public ResponseEntity<Void> followUser(@PathVariable Long userId, @PathVariable Long followId, @RequestHeader("Authorization") String token) {
        if (tokenService.isAuthorized(token,userId)){
        }else{
            return ResponseEntity.status(403).build();
        }
        userService.followUser(userId, followId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{userId}/unfollow/{unfollowId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId, @PathVariable Long unfollowId, @RequestHeader("Authorization") String token) {
        if (tokenService.isAuthorized(token,userId)){
        }else{
            return ResponseEntity.status(403).build();
        }
        userService.unfollowUser(userId, unfollowId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}