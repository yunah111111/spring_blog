//package com.tenco.spring_blog.Test;
//
//import com.tenco.spring_blog._core.config.IpBlockConfig;
//import com.tenco.spring_blog._core.error.Exception403;
//import com.tenco.spring_blog._core.interceptor.IpBlockInterceptor;
//import com.tenco.spring_blog._core.util.Define;
//import com.tenco.spring_blog.board.Board;
//import com.tenco.spring_blog.user.User;
//import com.tenco.spring_blog.user.UserRequest;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//
//@Slf4j
//@RequiredArgsConstructor
//@Controller
//public class IpBlockController {
//
//    // POST http://localhost:8080/login
//    // 로그인 처리 (예외적으로 POTS 요청)
//    @PostMapping("/login")
//    public String login(IpBlockConfig ipBlockConfig) {
//        if(ipBlockConfig) {
//            throw new Exception403("차단된 IP입니다.");
//        }
//        return "redirect:/";
//    }
//}
