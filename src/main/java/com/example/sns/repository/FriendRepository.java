package com.example.sns.repository;

import com.example.sns.domain.entity.Friend;
import com.example.sns.domain.entity.FriendStatus;
import com.example.sns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendStatus status);

    List<Friend> findAllByReceiverAndStatus(User receiver, FriendStatus status);


    @Query("select f from Friend f" +
            " where (f.receiver = ?1 OR f.sender = ?1)" +
            " and f.status='ACCEPTED'")
    List<Friend> findFriendByUser(User user);
}
