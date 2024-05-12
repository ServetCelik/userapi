package com.twix.userapi.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_followings",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    @JsonIgnoreProperties({"followers", "followings"})
    @Builder.Default
    private Set<UserEntity> followings = new HashSet<>();

    @ManyToMany(mappedBy = "followings", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"followers", "followings"})
    @Builder.Default
    private Set<UserEntity> followers = new HashSet<>();
}
