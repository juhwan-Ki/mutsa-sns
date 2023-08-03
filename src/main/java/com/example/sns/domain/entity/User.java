package com.example.sns.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String password;
    private String profileImg;
    private String email;
    private String phone;

    //== 연관관계 ==//
    @OneToMany(mappedBy = "user")
    private List<Article> articles = new ArrayList<>();

}
