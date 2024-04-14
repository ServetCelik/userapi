package com.twix.userapi.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String userName;
    public String password;
}





//@Entity
//@Table(name = "user")
//@Builder
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//public class UserEntity {
//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy= GenerationType.AUTO)
//    private Long id;
//
//    @Column(name = "user_name", nullable = false, unique = true)
//    private String userName;
//
//
//    @Column(name = "password")
//
//    private String password;
//
//
//}
