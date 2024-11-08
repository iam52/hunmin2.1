package com.hunmin.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comments;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String location;

    private Double latitude;

    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "board_image_urls", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "image_urls", columnDefinition = "TEXT", nullable = false)
    @Size(max = 10)
    private List<String> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private final List<Comment> comments = new ArrayList<>();

    public void changeTitle(@NotBlank String title) {
        this.title = Objects.requireNonNull(title, "제목은 필수입니다.");
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeLocation(String location) {
        this.location = location;
    }

    public void changeLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void changeLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void changeImgUrls(List<String> imageUrls) {
        this.imageUrls = new ArrayList<>(imageUrls);
    }

    public String getNickname() {
        return this.member.getNickname();
    }

    public void addImageUrl(String url) {
        this.imageUrls.add(url);
    }

    public List<String> getImageUrls() {
        return Collections.unmodifiableList(imageUrls);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public List<Comment> getComments(int page, int size) {
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, comments.size());
        return new ArrayList<>(comments.subList(fromIndex, toIndex));
    }
}
