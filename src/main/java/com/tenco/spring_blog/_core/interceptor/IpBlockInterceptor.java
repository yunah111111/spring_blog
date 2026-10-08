package com.tenco.spring_blog._core.interceptor;

import com.tenco.spring_blog._core.error.Exception401;
import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// 특정 IP를 차단 하는 인터셉터를 구현하기 단, 여러 개 가능(조원들 IP차단)
@Component
public class IpBlockInterceptor implements HandlerInterceptor {
    // 요청 -> 인터셉터(로그인여부) -> 컨트롤러(권한확인)
    // ip 차단


    // 컨트롤러 실행 전
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        if (ip.equals("192.168.5.11")) {
            throw new Exception403("차단된 IP주소입니다.");
        }
        return true;

    }

    // 컨트롤러 실행 후
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    // 요청 처리 완료 후
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
