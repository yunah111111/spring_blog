package com.tenco.spring_blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    // given에서 반복해서 사용할 테스트 사용자 생성 메서드
    private User 사용자_생성(String username, String password, String email) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build(); // 확인 필요: User에 해당 빌더와 필드가 있다고 가정
    }

    @Test
    @DisplayName("findByUsernameWithRoles - 존재하는 사용자 조회 및 roles 로딩")
    void findByUsernameWithRoles_존재하는_사용자_테스트() {
        // given: 테스트에 필요한 사용자를 직접 저장한다
        User user = userJpaRepository.save(
                사용자_생성("ssar", "1234", "ssar@test.com")
        );
        Long userId = user.getId();

        // when: 영속성 컨텍스트를 비우고 DB에서 다시 조회한다
        em.flush();
        em.clear();

        User foundUser = userJpaRepository
                .findByUsernameWithRoles("ssar")
                .orElseThrow();

        // then: 사용자가 조회되고 roles 연관 객체가 로딩되었는지 확인한다
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();

        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername()).isEqualTo("ssar");
        assertThat(util.isLoaded(foundUser, "roles")).isTrue();
    }

    @Test
    @DisplayName("findByUsernameWithRoles - 없는 사용자 조회")
    void findByUsernameWithRoles_존재하지않는_사용자_테스트() {
        // given: 조회할 사용자를 저장하지 않는다

        // when: 존재하지 않는 사용자명으로 조회한다
        Optional<User> result =
                userJpaRepository.findByUsernameWithRoles("unknown");

        // then: 조회 결과가 비어 있는지 확인한다
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsernameAndPassword - 사용자명과 비밀번호 일치")
    void findByUsernameAndPassword_일치하는_정보_테스트() {
        // given: 사용자명과 비밀번호를 가진 사용자를 저장한다
        userJpaRepository.save(
                사용자_생성("ssar", "1234", "ssar@test.com")
        );

        // when: 저장된 사용자명과 비밀번호로 조회한다
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword("ssar", "1234");

        // then: 조회에 성공했는지 확인한다
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("findByUsernameAndPassword - 비밀번호 불일치")
    void findByUsernameAndPassword_비밀번호가_다른_테스트() {
        // given: 특정 비밀번호를 가진 사용자를 저장한다
        userJpaRepository.save(
                사용자_생성("ssar", "1234", "ssar@test.com")
        );

        // when: 다른 비밀번호로 조회한다
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword("ssar", "9999");

        // then: 조회 결과가 없는지 확인한다
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsernameAndPassword - 존재하지 않는 사용자")
    void findByUsernameAndPassword_존재하지않는_사용자_테스트() {
        // given: 사용자를 저장하지 않는다

        // when: 존재하지 않는 사용자명으로 조회한다
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword("unknown", "1234");

        // then: 조회 결과가 없는지 확인한다
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsername - 존재하는 사용자명")
    void findByUsername_일치하는_사용자명_테스트() {
        // given: 사용자명을 가진 사용자를 저장한다
        userJpaRepository.save(
                사용자_생성("ssar", "1234", "ssar@test.com")
        );

        // when: 해당 사용자명으로 조회한다
        Optional<User> result = userJpaRepository.findByUsername("ssar");

        // then: 해당 사용자가 조회되는지 확인한다
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("findByUsername - 존재하지 않는 사용자명")
    void findByUsername_존재하지않는_사용자명_테스트() {
        // given: 사용자를 저장하지 않는다

        // when: 없는 사용자명으로 조회한다
        Optional<User> result = userJpaRepository.findByUsername("unknown");

        // then: 조회 결과가 없는지 확인한다
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEmail - 존재하는 이메일")
    void findByEmail_일치하는_이메일_테스트() {
        // given: 이메일을 가진 사용자를 저장한다
        userJpaRepository.save(
                사용자_생성("ssar", "1234", "ssar@test.com")
        );

        // when: 해당 이메일로 조회한다
        Optional<User> result = userJpaRepository.findByEmail("ssar@test.com");

        // then: 해당 사용자가 조회되는지 확인한다
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ssar@test.com");
    }

    @Test
    @DisplayName("findByEmail - 존재하지 않는 이메일")
    void findByEmail_존재하지않는_이메일_테스트() {
        // given: 사용자를 저장하지 않는다

        // when: 없는 이메일로 조회한다
        Optional<User> result = userJpaRepository.findByEmail("unknown@test.com");

        // then: 조회 결과가 없는지 확인한다
        assertThat(result).isEmpty();
    }
}