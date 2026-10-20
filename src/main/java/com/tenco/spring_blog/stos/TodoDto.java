package com.tenco.spring_blog.stos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// 외부 서버에서는 우리 서버에 불필요한 내용까지 많은 데이터를 전달한다.
// 불필요 필드 즉, 선언이 안된(우리코드) 데이터 무시하라는 뜻 (안전상의 이유로 선언을 권장)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoDto {
    // JSON의 key값은 key 이름과 필드 이름이 같아야 값이 들어온다.
    private Integer userId;
    private Long id;
    private String title;
    private boolean completed;

}
