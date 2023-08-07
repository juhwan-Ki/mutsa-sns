package com.example.sns.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    @Setter
    private String profileImg;
    private String email;
    private String phone;

    //== 연관관계 ==//
    @OneToMany(mappedBy = "user")
    private List<Article> articles = new ArrayList<>();

    @Builder
    public User(Long id, String username, String password, String profileImg, String email, String phone, List<Article> articles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.profileImg = profileImg;
        this.email = email;
        this.phone = phone;
        this.articles = articles;
    }
}
