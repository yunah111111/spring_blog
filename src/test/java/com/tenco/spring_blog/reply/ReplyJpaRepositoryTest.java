package com.tenco.spring_blog.reply;

import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.board.BoardJpaRepository;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReplyJpaRepositoryTest {

    @Autowired
    private ReplyJpaRepository replyJpaRepository;

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
                        .build() // 확인 필요: User에 해당 빌더와 필드가 있다고 가정
        );
    }

    // 테스트용 게시글 생성
    private Board 게시글_생성(User user, String title) {
        return boardJpaRepository.save(
                Board.builder()
                        .title(title)
                        .content("테스트 내용")
                        .user(user)
                        .build() // 확인 필요: Board에 title, content, user 빌더가 있다고 가정
        );
    }

    // 테스트용 댓글 생성
    private Reply 댓글_생성(Board board, User user, String comment) {
        return replyJpaRepository.save(
                Reply.builder()
                        .board(board)
                        .user(user)
                        .comment(comment)
                        .build() // 확인 필요: Reply의 댓글 필드명이 comment라고 가정
        );
    }

    @Test
    @DisplayName("findAllByBoardIdJoinUser - 댓글 목록과 작성자 조회")
    void findAllByBoardIdJoinUser_댓글목록_조회_테스트() {
        // given: 사용자, 게시글, 댓글 2개를 저장한다
        User user = 사용자_생성("ssar");
        Board board = 게시글_생성(user, "첫 번째 게시글");

        댓글_생성(board, user, "첫 번째 댓글");
        댓글_생성(board, user, "두 번째 댓글");

        em.flush();
        em.clear();

        // when: 게시글 ID로 댓글 목록을 조회한다
        List<Reply> replies =
                replyJpaRepository.findAllByBoardIdJoinUser(board.getId());

        // then: 댓글 개수와 오래된 순서, 작성자 로딩을 확인한다
        assertThat(replies).hasSize(2);

        assertThat(replies.get(0).getComment()).isEqualTo("첫 번째 댓글");
        assertThat(replies.get(1).getComment()).isEqualTo("두 번째 댓글");

        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();

        assertThat(util.isLoaded(replies.get(0), "user")).isTrue();
        assertThat(util.isLoaded(replies.get(1), "user")).isTrue();
    }

    @Test
    @DisplayName("findAllByBoardIdJoinUser - 댓글이 없는 게시글")
    void findAllByBoardIdJoinUser_댓글이없는_게시글_테스트() {
        // given: 댓글이 없는 게시글을 저장한다
        User user = 사용자_생성("ssar");
        Board board = 게시글_생성(user, "댓글 없는 게시글");

        // when: 해당 게시글의 댓글 목록을 조회한다
        List<Reply> replies =
                replyJpaRepository.findAllByBoardIdJoinUser(board.getId());

        // then: 빈 리스트가 반환되는지 확인한다
        assertThat(replies).isEmpty();
    }

    @Test
    @DisplayName("findAllByBoardIdJoinUser - 다른 게시글 댓글 제외")
    void findAllByBoardIdJoinUser_다른게시글_댓글_제외_테스트() {
        // given: 게시글 2개에 각각 댓글을 저장한다
        User user = 사용자_생성("ssar");

        Board firstBoard = 게시글_생성(user, "첫 번째 게시글");
        Board secondBoard = 게시글_생성(user, "두 번째 게시글");

        댓글_생성(firstBoard, user, "첫 번째 게시글 댓글");
        댓글_생성(secondBoard, user, "두 번째 게시글 댓글");

        // when: 첫 번째 게시글의 댓글만 조회한다
        List<Reply> replies =
                replyJpaRepository.findAllByBoardIdJoinUser(firstBoard.getId());

        // then: 첫 번째 게시글의 댓글만 반환되는지 확인한다
        assertThat(replies).hasSize(1);
        assertThat(replies.get(0).getComment())
                .isEqualTo("첫 번째 게시글 댓글");
        assertThat(replies.get(0).getBoard().getId())
                .isEqualTo(firstBoard.getId());
    }

    @Test
    @DisplayName("deleteByBoardId - 특정 게시글 댓글 일괄 삭제")
    void deleteByBoardId_게시글_댓글_일괄삭제_테스트() {
        // given: 사용자와 게시글 2개, 각 게시글의 댓글을 저장한다
        User user = 사용자_생성("ssar");

        Board firstBoard = 게시글_생성(user, "삭제할 게시글");
        Board secondBoard = 게시글_생성(user, "유지할 게시글");

        댓글_생성(firstBoard, user, "삭제할 댓글 1");
        댓글_생성(firstBoard, user, "삭제할 댓글 2");
        댓글_생성(secondBoard, user, "유지할 댓글");

        em.flush();

        // when: 첫 번째 게시글에 달린 댓글을 일괄 삭제한다
        replyJpaRepository.deleteByBoardId(firstBoard.getId());

        em.flush();
        em.clear();

        // then: 삭제 대상 게시글의 댓글이 없는지 확인한다
        List<Reply> deletedBoardReplies =
                replyJpaRepository.findAllByBoardIdJoinUser(firstBoard.getId());

        assertThat(deletedBoardReplies).isEmpty();

        // then: 두 번째 게시글의 댓글은 유지되는지 확인한다
        List<Reply> remainingBoardReplies =
                replyJpaRepository.findAllByBoardIdJoinUser(secondBoard.getId());

        assertThat(remainingBoardReplies).hasSize(1);
        assertThat(remainingBoardReplies.get(0).getComment())
                .isEqualTo("유지할 댓글");
    }
}