
package com.tenco.spring_blog.board;

import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
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

    // 테스트용 사용자 생성
    private User createUser(String username) {
        return User.builder()
                .username(username)
                .email(username + "@test.com")
                .password("1234")
                .build();
    }

    // 테스트용 게시글 생성
    private Board createBoard(String title, User user) {
        return Board.builder()
                .title(title)
                .content("테스트 내용")
                .user(user)
                .build();
    }

    @Test
    @DisplayName("게시글 ID로 조회하면 작성자도 함께 조회된다")
    void findByIdJoinUser_test() {
        // Given
        User user = userJpaRepository.save(createUser("ssar"));

        Board board = boardJpaRepository.save(
                createBoard("테스트 게시글", user)
        );

        em.flush();
        em.clear();

        // When
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(board.getId());

        // Then
        assertThat(result).isPresent();

        Board foundBoard = result.get();

        assertThat(foundBoard.getTitle()).isEqualTo("테스트 게시글");
        assertThat(foundBoard.getUser().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("전체 게시글 조회 시 작성자 정보도 함께 조회된다")
    void findAllJoinUser_test() {
        // Given
        User user1 = userJpaRepository.save(createUser("testUser1"));
        User user2 = userJpaRepository.save(createUser("testUser2"));

        boardJpaRepository.save(
                createBoard("테스트 게시글1", user1)
        );
        boardJpaRepository.save(
                createBoard("테스트 게시글2", user2)
        );

        em.flush();
        em.clear();

        // When
        List<Board> boards = boardJpaRepository.findAllJoinUser();

        // Then
        assertThat(boards)
                .extracting(Board::getTitle)
                .contains("테스트 게시글1", "테스트 게시글2");

        assertThat(boards)
                .filteredOn(board ->
                        board.getTitle().equals("테스트 게시글1")
                                || board.getTitle().equals("테스트 게시글2"))
                .extracting(board -> board.getUser().getUsername())
                .containsExactlyInAnyOrder("testUser1", "testUser2");
    }
}
