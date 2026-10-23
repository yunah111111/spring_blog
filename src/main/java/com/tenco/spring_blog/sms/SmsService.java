package com.tenco.spring_blog.sms;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import com.tenco.spring_blog._core.error.Exception500;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog._core.util.MailUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${solapi.api-key}")
    private String apiKey;
    @Value("${solapi.api-secret}")
    private String apiSecret;
    @Value("${solapi.sender}")
    private String sender;


    public void sendVerificationCode(String num, HttpSession session) {

        String code = MailUtil.generateCode();

        DefaultMessageService messageService =
                SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);

        try {
            Message message = new Message();
            message.setFrom(sender);
            message.setTo(num);
            message.setText("[스프링블로그] 인증번호 " + code + "\n(5분 안에 입력)");

            // 1-3. 발송 (실패하면)
            messageService.send(message);

        } catch (Exception e) {
            throw new Exception500("문자 작성 중 오류가 발생했습니다");
        }

        session.setAttribute(Define.NUM_CODE_PREFIX + num, code);
        log.info("인증번호 발송 완료 - 번호 :  {}", num); // 인증 번호 자체는 로그에 안남기는 게 좋음

    }

    // 2. 인증번호 확인 : 일치하면 true
    public boolean verifyCode(String num, String code, HttpSession session) {
        String savedCode = (String) session.getAttribute(Define.NUM_CODE_PREFIX + num);
        // emailCode_a@naver.com : 123456
        if (savedCode == null || !savedCode.equals(code)) {
            return false;
        }
        // 사용한 인증번호는 삭제 (한 번만 쓸 수 있게)
        session.removeAttribute(Define.NUM_CODE_PREFIX + num);
        // 인증을 마친 전화번호를 세션에 기록해야 함 (회원가입 시 서버가 확인) - 인증된 전화번호가 아닌 변조해서 던질 수 있음
        session.setAttribute(Define.VERIFIED_NUM, num);
        return true;
    }
}
