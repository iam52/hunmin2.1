package com.hunmin.domain.repository;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.entity.MemberLevel;
import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.domain.notice.entity.Notice;

import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.domain.notice.repository.NoticeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class NoticeRepositoryTest {
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;
    private Notice savedNotice;
    private List<Member> savedMembers = new ArrayList<>();
    private List<Notice> savedNotices = new ArrayList<>();

    @BeforeEach
    @DisplayName("더미데이터")
    public void setUp() {
        for(int i=1; i<=10;i++) {//1~5 까지 관리자 더미, 나머지 유저 더미
            Member member = Member.builder()
                    .memberRole(i <= 5 ? MemberRole.ADMIN : MemberRole.USER)
                    .nickname(i <= 5 ? "관리자이름" + i : "유저이름" + i)
                    .country(i <= 5 ? "관리자국적" + i : "유저국적" + i)
                    .email(i <= 5 ? "관리자이멜" + i : "유저이멜" + i)
                    .image(i <= 5 ? "관리자아바타" + i : "유저아바타" + i)
                    .level(MemberLevel.ADVANCED)
                    .password(i <= 5 ? "관리자비밀번호" + i : "유저비밀번호" + i)
                    .build();
            savedMember = memberRepository.save(member);
            savedMembers.add(savedMember);
            // 데이터 저장 확인
            //System.out.println("멤버 Id: " + savedMember.getMemberId() + " - " + savedMember.getNickname());

            if(i<=5){ //공지사항 작성더미
                Notice notice = Notice.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .member(savedMember)
                        .build();
                savedNotice =noticeRepository.save(notice);
                savedNotices.add(savedNotice);
                // 데이터 저장 확인
                //System.out.println("공지사항 Id: " + savedNotice.getNoticeId() + " - " + savedNotice.getTitle());
            }
        }
    }

    @AfterEach
    @DisplayName("DB초기화")
    public void clear(){
        noticeRepository.deleteAll();
        memberRepository.deleteAll();

    }

    @Test
    @DisplayName("Notice 리스트가 조회가 되는지 확인")
    void getAllNoticesResponse() {
        //given
        Pageable pageable = PageRequest.of(0,10, Sort.by("noticeId").descending());

        //when
        Page<Notice> noticeListPage = noticeRepository.findAll(pageable);

        // then
        assertThat(noticeListPage).isNotNull();
        assertThat(noticeListPage.getTotalElements()).isEqualTo(5); // 총 요소 수를 검증
        assertThat(noticeListPage.getSize()).isEqualTo(10); // 첫 페이지의 크기를 검증
        assertThat(noticeListPage.getContent().get(0).getTitle()).isEqualTo("제목10"); //최신 공지사항의 제목을 검증
        assertThat(noticeListPage.getContent().get(0).getMember().getNickname()).isEqualTo("관리자이름10"); // 최신 공지사항의 작성자를 검증
    }

    @Test
    @DisplayName("Notice 객체가 조회가 되는지 확인")
    void testRead() {
        //given
        Long noticeId1= savedNotices.get(3).getNoticeId();

        //when
        Notice findNotice1 = noticeRepository.findById(noticeId1)
                .orElseThrow(() -> new NoSuchElementException("Notice not found with id "));


        //then
        assertThat(findNotice1).isNotNull();
        assertThat(findNotice1.getNoticeId()).isEqualTo(noticeId1);
    }

    @Test
    @DisplayName("Notice가 등록되는지 확인")
    void testSave() {

        //given
        Member admin = savedMembers.get(3);

        Notice notice1 = Notice.builder()
                .title("공지등록테스트")
                .content("공지내용테스트")
                .member(admin)
                .build();

        //when
        Notice savedNoticeNo1 = noticeRepository.save(notice1);

        //then
        assertThat(savedNoticeNo1).isNotNull();
        assertThat(savedNoticeNo1.getNoticeId()).isEqualTo(notice1.getNoticeId());
        assertThat(savedNoticeNo1.getTitle()).isEqualTo(notice1.getTitle());
        assertThat(savedNoticeNo1.getContent()).isEqualTo(notice1.getContent());
        assertThat(savedNoticeNo1.getMember().getNickname()).isEqualTo(notice1.getMember().getNickname());
    }

    @Test
    @DisplayName("공지가 수정되는지 확인")
    void testUpdate() {
        //given
        Long noticeId1 = savedNotices.get(4).getNoticeId();
        Long memberId1 = savedMembers.get(3).getMemberId();

        //when
        Notice findNotice = noticeRepository.findById(noticeId1)
                .orElseThrow(() -> new NoSuchElementException("Notice not found with id "));
        Member findMember = memberRepository.findById(memberId1)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id "));

        findNotice.changeMember(findMember);
        findNotice.changeTitle("제목수정");
        findNotice.changeContent("내용수정");

        //then
        assertThat(findNotice.getMember()).isEqualTo(findMember); //어떤 관리자가 수정했는지
        assertThat(findNotice.getTitle()).isEqualTo("제목수정");
        assertThat(findNotice.getContent()).isEqualTo("내용수정");
    }

    @Test
    @DisplayName("공지가 삭제되는지 확인")
    void testDelete() {
        //given
        Long noticeId = savedNotices.get(3).getNoticeId();
        //when
        noticeRepository.deleteById(noticeId);
        //then
        assertThat(noticeRepository.findById(noticeId)).isEmpty();
        }
    }
