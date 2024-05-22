package com.twix.userapi.business;


import com.google.gson.Gson;
import com.twix.userapi.business.exceptions.InvalidCredentialsException;
import com.twix.userapi.business.exceptions.UserNameAlreadyExistsException;
import com.twix.userapi.business.exceptions.UserNotExistException;
import com.twix.userapi.controller.CreateUserRequest;
import com.twix.userapi.repository.UserEntity;
import com.twix.userapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    @Override
    public Long createUser(CreateUserRequest user) {
        log.info("Creating user with username: {}", user.getUserName());

        if (checkIfUserExistsByUserName(user.getUserName())) {
            log.warn("Username {} already exists", user.getUserName());
            throw new UserNameAlreadyExistsException("Username " + user.getUserName() + " already exists.");
        }


         String encodedPassword = passwordEncoder.encode(user.getPassword());
//        String encodedPassword = "1234";

        UserEntity savedUser = userRepository.save(UserEntity.builder()
                .userName(user.getUserName())
                .password(encodedPassword)
                .build());

        log.info("User created with ID: {}", savedUser.getId());

        String jsonUser = userToString(UserSharable.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .build());

        sendWithRabbitMQ(jsonUser);
        return savedUser.getId();
    }

    @Override
    public Optional<String> loginUser(String name, String password) {
        Optional<UserEntity> userOptional = getUserByUserName(name);

        if (userOptional.isEmpty()) {
            log.warn("Username {} does not exist", name);
            throw new UserNotExistException("Username " + name + " does not exist.");
        }

        UserEntity user = userOptional.get();

        if (!matchesPassword(password, user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect username or password. Please try again.");
        }

        UserSharable userSharable = UserSharable.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Correct way to set content type

        HttpEntity<UserSharable> requestEntity = new HttpEntity<>(userSharable, headers);

        String tokenUrl = "http://auth-api-service:8083/auth/generateAccessToken";
        Optional<String> token = Optional.ofNullable(restTemplate.postForObject(tokenUrl, requestEntity, String.class));
        return token;
    }

    private boolean checkIfUserExistsByUserName(String name) {
        boolean exists = userRepository.existsByUserName(name);
        log.debug("Check if user exists by username {}: {}", name, exists);
        return exists;
    }

    private boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    @Override
    public Long deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!checkIfUserExistsById(id)) {
            log.warn("User with ID {} does not exist", id);
            throw new UserNotExistException("User with ID " + id + " does not exist.");
        }

        userRepository.deleteById(id);
        log.info("User with ID {} deleted", id);
        return id;
    }

    private boolean checkIfUserExistsById(Long id) {
        boolean exists = userRepository.existsById(id);
        log.debug("Check if user exists by ID {}: {}", id, exists);
        return exists;
    }

    @Transactional
    @Override
    public Long updateUser(Long id, UserEntity userEntity) {
        log.info("Updating user with ID: {}", id);

        if (!checkIfUserExistsById(id)) {
            log.warn("User with ID {} does not exist", id);
            throw new UserNotExistException("User with ID " + id + " does not exist.");
        }

        UserEntity updatedUser = userRepository.save(userEntity);
        log.info("User with ID {} updated", id);
        return updatedUser.getId();
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);

        if (!checkIfUserExistsById(id)) {
            log.warn("User with ID {} does not exist", id);
            throw new UserNotExistException("User with ID " + id + " does not exist.");
        }

        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> getUserByUserName(String name) {
        log.info("Fetching user with username: {}", name);
        return Optional.ofNullable(userRepository.findByUserName(name));
    }

    @Override
    public List<UserEntity> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Transactional
    public void followUser(Long userId, Long followId) {
        log.info("User with ID {} following user with ID {}", userId, followId);

        Optional<UserEntity> userOpt = userRepository.findById(userId);
        Optional<UserEntity> followOpt = userRepository.findById(followId);

        if (userOpt.isPresent() && followOpt.isPresent()) {
            UserEntity user = userOpt.get();
            UserEntity follow = followOpt.get();

            user.getFollowings().add(follow);
            userRepository.save(user);

            log.info("User with ID {} now follows user with ID {}", userId, followId);
        } else {
            log.warn("User with ID {} or user with ID {} not found", userId, followId);
        }
    }

    @Transactional
    public void unfollowUser(Long userId, Long unfollowId) {
        log.info("User with ID {} unfollowing user with ID {}", userId, unfollowId);

        Optional<UserEntity> userOpt = userRepository.findById(userId);
        Optional<UserEntity> unfollowOpt = userRepository.findById(unfollowId);

        if (userOpt.isPresent() && unfollowOpt.isPresent()) {
            UserEntity user = userOpt.get();
            UserEntity unfollow = unfollowOpt.get();

            user.getFollowings().remove(unfollow);
            userRepository.save(user);

            log.info("User with ID {} no longer follows user with ID {}", userId, unfollowId);
        } else {
            log.warn("User with ID {} or user with ID {} not found", userId, unfollowId);
        }
    }

    private String userToString(UserSharable user) {
        log.debug("Converting user to JSON: {}", user);
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    private void sendWithRabbitMQ(String message) {
        log.info("Sending message to RabbitMQ: {}", message);
        rabbitTemplate.convertAndSend(exchange.getName(), "user_created", message);
    }
}
