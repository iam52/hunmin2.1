package com.hunmin.domain.follow.repository.search;

import com.hunmin.domain.follow.dto.FollowRequestDTO;
import com.hunmin.domain.follow.entity.QFollow;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.hunmin.domain.member.entity.QMember.member;


@Log4j2
public class FollowSearchImpl implements FollowSearch {

    private final JPQLQueryFactory queryFactory;

    public FollowSearchImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<FollowRequestDTO> getFollowPage(Long memberId, Pageable pageable) {

        // 서로 다른 별칭을 가진 QFollow 인스턴스 생성
        QFollow followeeFollow = new QFollow("followeeFollow");
        QFollow followerFollow = new QFollow("followerFollow");

        List<FollowRequestDTO> results = queryFactory
                .select(Projections.bean(FollowRequestDTO.class,
                        followeeFollow.followId,
                        followeeFollow.follower.memberId.as("followerId"),
                        followeeFollow.followee.memberId.as("followeeId"),
                        followeeFollow.notification,
                        followeeFollow.isBlock,
                        followeeFollow.status,
                        followeeFollow.follower.nickname.as("followerName"),
                        followeeFollow.follower.email.as("followerEmail"),
                        followeeFollow.follower.image.as("followerImage"),
                        followeeFollow.followee.nickname.as("followeeName"),
                        followeeFollow.followee.email.as("followeeEmail"),
                        followeeFollow.followee.image.as("followeeImage"),
                        followeeFollow.createdAt)).distinct()
                .from(member)
                .leftJoin(member.followees, followeeFollow)
                .leftJoin(member.followers, followerFollow)
                .where(member.memberId.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info("results{}", results.toArray());

        // 전체 갯수 계산 시 특정 요건에 맞게 쿼리 수정 필요할 수 있음
        Long total = queryFactory
                .select(followeeFollow.count())
                .from(member)
                .leftJoin(member.followees, followeeFollow)
                .leftJoin(member.followers, followerFollow)
                .where(member.memberId.eq(memberId))
                .fetchOne();
        log.info("total{}", total);

        long totalCount = total == null ? 0L : total;

        return new PageImpl<>(results, pageable, totalCount);
    }
}
