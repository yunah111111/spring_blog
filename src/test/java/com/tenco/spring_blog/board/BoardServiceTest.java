package com.tenco.spring_blog.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardJpaRepository boardJpaRepository;

    @Mock
    private com.tenco.spring_blog.user.UserJpaRepository userJpaRepository;

    @Mock
    private com.tenco.spring_blog.reply.ReplyJpaRepository replyJpaRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    void 게시글_페이징_조회() {
        // given: 빈 페이지를 반환하도록 설정
        when(boardJpaRepository.findAllJoinUserPage(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    return new PageImpl<Board>(
                            List.of(),
                            pageable,
                            0
                    );
                });

        // when: 1페이지, 페이지당 5개 조회
        BoardResponse.PageDto result =
                boardService.findAllPage(1, 5);

        // then: 결과가 존재하고, Repository에 올바른 페이지 정보가 전달됨
        assertNotNull(result);

        verify(boardJpaRepository).findAllJoinUserPage(
                argThat(pageable ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 5
                )
        );
    }
}