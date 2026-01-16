package com.chung.taskcrud.auth.service.impl;

import com.chung.taskcrud.auth.service.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void sendVerifyEmail(String to, String verifyLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Verify your email - TaskCRUD");

            String html = """
                <div style="font-family:Arial,sans-serif;line-height:1.6">
                  <h2>Xác thực email</h2>
                  <p>Bạn vừa đăng ký tài khoản. Vui lòng bấm nút bên dưới để xác thực email:</p>
                  <p>
                    <a href="%s"
                       style="display:inline-block;padding:10px 16px;background:#111827;color:#fff;
                              text-decoration:none;border-radius:8px">
                      Xác thực email
                    </a>
                  </p>
                  <p>Nếu không bấm được nút, hãy copy link này:</p>
                  <p><a href="%s">%s</a></p>
                  <p style="color:#6b7280;font-size:12px">Link có thể hết hạn sau 30 phút.</p>
                </div>
            """.formatted(verifyLink, verifyLink, verifyLink);

            helper.setText(html, true);

            mailSender.send(message);
            log.info("VERIFY EMAIL SENT => to={}", to);
        } catch (Exception e) {
            log.error("Failed to send verify email to={}", to, e);
        }
    }
}
