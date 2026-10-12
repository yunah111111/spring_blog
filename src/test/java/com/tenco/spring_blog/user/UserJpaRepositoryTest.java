package com.tenco.spring_blog.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    // 테스트용 사용자 생성 메서드
    private User createUser(String username, String email, String password) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }

    @Test
    @DisplayName("사용자 등록 테스트")
    void save_test() {
        // Given
        User user = createUser("saveUser", "save@test.com", "1234");

        // When
        User savedUser = userJpaRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("saveUser");
    }

    @Test
    @DisplayName("ID로 사용자 조회 테스트")
    void findById_test() {
        // Given
        User user = userJpaRepository.save(
                createUser("findIdUser", "findId@test.com", "1234")
        );

        // When
        Optional<User> result = userJpaRepository.findById(user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("findIdUser");
    }

    @Test
    @DisplayName("전체 사용자 조회 테스트")
    void findAll_test() {
        // Given
        userJpaRepository.save(
                createUser("allUser1", "all1@test.com", "1234")
        );
        userJpaRepository.save(
                createUser("allUser2", "all2@test.com", "5678")
        );

        // When
        List<User> users = userJpaRepository.findAll();

        // Then
        assertThat(users)
                .extracting(User::getUsername)
                .contains("allUser1", "allUser2");
    }

    @Test
    @DisplayName("사용자명과 비밀번호로 조회 테스트")
    void findByUsernameAndPassword_test() {
        // Given
        userJpaRepository.save(
                createUser("loginUser", "login@test.com", "1234")
        );

        // When
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword("loginUser", "1234");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("loginUser");
    }

    @Test
    @DisplayName("사용자명으로 조회 테스트")
    void findByUsername_test() {
        // Given
        userJpaRepository.save(
                createUser("usernameUser", "username@test.com", "1234")
        );

        // When
        Optional<User> result =
                userJpaRepository.findByUsername("usernameUser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("usernameUser");
    }

    @Test
    @DisplayName("이메일로 조회 테스트")
    void findByEmail_test() {
        // Given
        userJpaRepository.save(
                createUser("emailUser", "email@test.com", "1234")
        );

        // When
        Optional<User> result =
                userJpaRepository.findByEmail("email@test.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("email@test.com");
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void deleteById_test() {
        // Given
        User user = userJpaRepository.save(
                createUser("deleteUser", "delete@test.com", "1234")
        );
        Long id = user.getId();

        // When
        userJpaRepository.deleteById(id);

        // Then
        assertThat(userJpaRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("사용자 존재 여부 확인 테스트")
    void existsById_test() {
        // Given
        User user = userJpaRepository.save(
                createUser("existsUser", "exists@test.com", "1234")
        );

        // When
        boolean result = userJpaRepository.existsById(user.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("사용자 데이터 개수 확인 테스트")
    void count_test() {
        // Given
        long beforeCount = userJpaRepository.count();

        userJpaRepository.save(
                createUser("countUser1", "count1@test.com", "1234")
        );
        userJpaRepository.save(
                createUser("countUser2", "count2@test.com", "5678")
        );

        // When
        long afterCount = userJpaRepository.count();

        // Then
        assertThat(afterCount - beforeCount).isEqualTo(2);
    }
}