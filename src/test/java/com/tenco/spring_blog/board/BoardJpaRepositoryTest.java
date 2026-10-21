package com.tenco.spring_blog.board;

import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BoardJpaRepositoryTest {

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    // 테스트용 사용자 생성
    private User 사용자_생성(String username) {
        return userJpaRepository.save(
                User.builder()
                        .username(username)
                        .password("1234")
                        .email(username + "@test.com")
                        .build()
        );
    }

    // 테스트용 게시글 생성
    private Board 게시글_생성(
            User user,
            String title,
            String content
    ) {
        return boardJpaRepository.save(
                Board.builder()
                        .title(title)
                        .content(content)
                        .user(user)
                        .build()
        );
    }

    @Test
    @DisplayName("findByIdJoinUser - 존재하는 게시글과 작성자 조회")
    void findByIdJoinUser_존재하는_게시글_테스트() {
        // given
        User user = 사용자_생성("ssar");
        Board board = 게시글_생성(
                user,
                "테스트 제목",
                "테스트 내용"
        );

        Long boardId = board.getId();

        em.flush();
        em.clear();

        // when
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(boardId);

        // then
        assertThat(result).isPresent();

        Board foundBoard = result.orElseThrow();

        assertThat(foundBoard.getId()).isEqualTo(boardId);
        assertThat(foundBoard.getTitle()).isEqualTo("테스트 제목");
        assertThat(foundBoard.getUser().getUsername()).isEqualTo("ssar");

        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();

        assertThat(util.isLoaded(foundBoard, "user")).isTrue();
    }

    @Test
    @DisplayName("findByIdJoinUser - 존재하지 않는 게시글")
    void findByIdJoinUser_존재하지않는_게시글_테스트() {
        // given
        Long 없는게시글ID = 999999L;

        // when
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(없는게시글ID);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllJoinUser - 페이징과 정렬")
    void findAllJoinUser_페이징과_정렬_테스트() {
        // given
        User user = 사용자_생성("pagingUser");

        게시글_생성(user, "페이징 첫 번째", "내용 1");
        게시글_생성(user, "페이징 두 번째", "내용 2");
        게시글_생성(user, "페이징 세 번째", "내용 3");
        게시글_생성(user, "페이징 네 번째", "내용 4");
        게시글_생성(user, "페이징 다섯 번째", "내용 5");

        em.flush();
        em.clear();

        PageRequest pageable = PageRequest.of(
                0,
                2,
                Sort.by(Sort.Direction.DESC, "id")
        );

        // when
        Page<Board> result =
                boardJpaRepository.findAllJoinUser(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(5);

        // ID 내림차순인지 확인
        assertThat(result.getContent().get(0).getId())
                .isGreaterThan(result.getContent().get(1).getId());

        // 작성자가 함께 조회되었는지 확인
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();

        assertThat(util.isLoaded(result.getContent().get(0), "user")).isTrue();
        assertThat(util.isLoaded(result.getContent().get(1), "user")).isTrue();
    }

    @Test
    @DisplayName("findAllKeywordJoinUser - 제목 검색")
    void findAllKeywordJoinUser_제목_검색_테스트() {
        // given
        User user = 사용자_생성("titleSearchUser");

        게시글_생성(
                user,
                "검색전용키워드XYZ 제목",
                "첫 번째 내용"
        );

        게시글_생성(
                user,
                "다른 제목",
                "두 번째 내용"
        );

        em.flush();
        em.clear();

        // when
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        PageRequest.of(0, 10),
                        "검색전용키워드XYZ"
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("검색전용키워드XYZ 제목");
    }

    @Test
    @DisplayName("findAllKeywordJoinUser - 내용 검색")
    void findAllKeywordJoinUser_내용_검색_테스트() {
        // given
        User user = 사용자_생성("contentSearchUser");

        게시글_생성(
                user,
                "첫 번째 제목",
                "검색전용내용XYZ"
        );

        게시글_생성(
                user,
                "두 번째 제목",
                "다른 내용"
        );

        em.flush();
        em.clear();

        // when
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        PageRequest.of(0, 10),
                        "검색전용내용XYZ"
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent())
                .isEqualTo("검색전용내용XYZ");
    }

    @Test
    @DisplayName("findAllKeywordJoinUser - 검색 결과 없음")
    void findAllKeywordJoinUser_검색결과_없음_테스트() {
        // given
        User user = 사용자_생성("noSearchUser");

        게시글_생성(
                user,
                "자바 입문",
                "객체지향 프로그래밍"
        );

        em.flush();
        em.clear();

        // when
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        PageRequest.of(0, 10),
                        "검색결과없는키워드XYZ"
                );

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }

    @Test
    @DisplayName("findAllKeywordJoinUser - 빈 키워드")
    void findAllKeywordJoinUser_빈_키워드_테스트() {
        // given
        User user = 사용자_생성("emptyKeywordUser");

        Board board1 = 게시글_생성(
                user,
                "빈 키워드 테스트 첫 번째",
                "내용 1"
        );

        Board board2 = 게시글_생성(
                user,
                "빈 키워드 테스트 두 번째",
                "내용 2"
        );

        Board board3 = 게시글_생성(
                user,
                "빈 키워드 테스트 세 번째",
                "내용 3"
        );

        em.flush();
        em.clear();

        // when
        Page<Board> result =
                boardJpaRepository.findAllKeywordJoinUser(
                        PageRequest.of(0, 100),
                        ""
                );

        // then
        assertThat(result.getContent())
                .extracting(Board::getId)
                .contains(
                        board1.getId(),
                        board2.getId(),
                        board3.getId()
                );
    }
}