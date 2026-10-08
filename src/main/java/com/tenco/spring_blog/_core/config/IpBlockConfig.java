package com.tenco.spring_blog._core.config;

import com.tenco.spring_blog._core.interceptor.IpBlockInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class IpBlockConfig implements WebMvcConfigurer {

    private final IpBlockInterceptor ipBlockInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipBlockInterceptor)
                .addPathPatterns("/**");
    }
}
