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
// 자주 검색되는 항목 인덱싱
@Table(indexes = {
        @Index(name = "idx_member_email", columnList = "email", unique = true),
})
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private String country;

    @Enumerated(EnumType.STRING)
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

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateCountry(String country) {
        this.country = country;
    }

    public void updateLevel(MemberLevel level) {
        this.level = level;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void addFollower(Follow follower) {
        followers.add(follower);
        follower.setFollower(this);
    }

    public void removeFollower(Follow follower) {
        followers.remove(follower);
        follower.setFollower(null);
    }
}
