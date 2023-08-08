package com.example.sns.repository;

import com.example.sns.domain.entity.Following;
import com.example.sns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> {
    boolean existsByFollowerAndFollowing(User user, User following);

    void deleteByFollowerAndFollowing(User user, User following);
}
