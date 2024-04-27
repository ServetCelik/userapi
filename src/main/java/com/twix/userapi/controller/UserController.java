package com.twix.userapi.controller;

import com.twix.userapi.business.UserService;
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
        return ResponseEntity.ok(userService.GetAllUsers());
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
}





//        rabbitTemplate.convertAndSend("userUpdated", "#", null, m -> {
//            m.getMessageProperties().getHeaders().put("MessageType", "CreateTweetEvent");
//            return m;
//        });
//return ResponseEntity.ok(userService.GetAllUsers());