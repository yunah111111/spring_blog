
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
        // Given - 테스트에 필요한 데이터 준비
        User user = createUser("ssar", "ssar@test.com", "1234");

        // When - 실제로 테스트할 기능 실행
        User savedUser = userJpaRepository.save(user);

        // Then - 실행 결과 검증
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("ID로 사용자 조회 테스트")
    void findById_test() {
        // Given
        User user = userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
        );

        // When
        Optional<User> result = userJpaRepository.findById(user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("전체 사용자 조회 테스트")
    void findAll_test() {
        // Given
        userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
        );
        userJpaRepository.save(
                createUser("cos", "cos@test.com", "5678")
        );

        // When
        List<User> users = userJpaRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("사용자명과 비밀번호로 조회 테스트")
    void findByUsernameAndPassword_test() {
        // Given
        userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
        );

        // When
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword("ssar", "1234");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("사용자명으로 조회 테스트")
    void findByUsername_test() {
        // Given
        userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
        );

        // When
        Optional<User> result = userJpaRepository.findByUsername("ssar");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("이메일로 조회 테스트")
    void findByEmail_test() {
        // Given
        userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
        );

        // When
        Optional<User> result = userJpaRepository.findByEmail("ssar@test.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ssar@test.com");
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void deleteById_test() {
        // Given
        User user = userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
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
                createUser("ssar", "ssar@test.com", "1234")
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
        userJpaRepository.save(
                createUser("ssar", "ssar@test.com", "1234")
        );
        userJpaRepository.save(
                createUser("cos", "cos@test.com", "5678")
        );

        // When
        long count = userJpaRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }
}
