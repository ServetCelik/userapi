package com.twix.userapi.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"followers", "followings"})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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



    public void addFollower(UserEntity toFollow) {
        followings.add(toFollow);
        toFollow.getFollowers().add(this);
    }

    public void removeFollower(UserEntity toFollow) {
        followings.remove(toFollow);
        toFollow.getFollowers().remove(this);
    }
}


