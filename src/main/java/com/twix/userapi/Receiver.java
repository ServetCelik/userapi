//package com.twix.userapi;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@AllArgsConstructor
//@Slf4j
//public class Receiver {
//    @RabbitListener(queues = "user_queue")
//    public void receiveMessage(String user) {
//        // Logic to handle user created event
//        System.out.println("UUUUUUUUUser created: " + user);
//        log.info("UUUUUUUUUUUUUUUUUUser creatednew message revicved to tweetapi:  {}", user);
//    }
//}
