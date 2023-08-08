package com.example.sns.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE following SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "following_id")
    private User following;

    @Builder
    public Following(Long id, LocalDateTime deletedAt, User follower, User following) {
        this.id = id;
        this.deletedAt = deletedAt;
        this.follower = follower;
        this.following = following;
    }
}
