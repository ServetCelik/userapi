package com.twix.userapi.business;


import com.google.gson.Gson;
import com.twix.userapi.controller.CreateUserRequest;
import com.twix.userapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.twix.userapi.repository.UserEntity;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService{
    private final UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange exchange;
    @Override
    public Long createUser(CreateUserRequest user){
        UserEntity savedUser = userRepository.save(UserEntity.builder()
                .userName(user.getUserName())
                .password(user.getPassword())
                .build());
        String jsonUser = userToString(UserSharable
                .builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .build());
        sendWithRabbitMQ(jsonUser);
        return savedUser.getId();
    }
    @Override
    public Long deleteUser(Long userId){
        userRepository.deleteById(userId);
        return userId;
    }

    @Override
    public Long updateUser(Long id, UserEntity userEntity) {
        return userRepository.save(userEntity).getId();
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }
    @Override
    public List<UserEntity> GetAllUsers(){
      return userRepository.findAll();
    }

    private String userToString(UserSharable user){
        Gson gson = new Gson();
        return gson.toJson(user);
    }
    private void sendWithRabbitMQ(String message){
        rabbitTemplate.convertAndSend(exchange.getName(),
                "user_created", message);
    }
}
