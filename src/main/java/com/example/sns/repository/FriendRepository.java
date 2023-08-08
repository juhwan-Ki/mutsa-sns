package com.example.sns.repository;

import com.example.sns.domain.entity.Friend;
import com.example.sns.domain.entity.FriendStatus;
import com.example.sns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendStatus status);

    List<Friend> findAllByReceiverAndStatus(User receiver, FriendStatus status);

    List<Friend> findFriendByUser(User user);
}
