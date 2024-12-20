package com.hunmin.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_member_email", columnList = "email", unique = true),
        @Index(name = "idx_member_nickname", columnList = "nickname", unique = true)
})
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberLevel level;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Builder.Default
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followers = new HashSet<>();

    @Builder.Default
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followees = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public void addFollower(Follow follower) {
        followers.add(follower);
        follower.setFollower(this);
    }

    public void removeFollower(Follow follower) {
        followers.remove(follower);
        follower.setFollower(null);
    }
}
