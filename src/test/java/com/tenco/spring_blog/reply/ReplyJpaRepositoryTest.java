package com.tenco.spring_blog.reply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReplyJpaRepositoryTest {

    @Autowired
    private ReplyJpaRepository replyJpaRepository;

    @Test
    public void findAllByBoardIdJoinUser_test() {
        // given - 테스트에 필요한 데이터 준비
        Long boardId = 1L;

        // when - 해당 게시글의 댓글 목록 조회
        List<Reply> replyList =
                replyJpaRepository.findAllByBoardIdJoinUser(boardId);

        // then - 조회 결과 확인
        assertThat(replyList).isNotNull();

        // 조회된 댓글 목록 출력
        replyList.forEach(reply -> {
            System.out.println("댓글 내용: " + reply.getComment());
            System.out.println("작성자: " + reply.getUser().getUsername());
        });
    }
}