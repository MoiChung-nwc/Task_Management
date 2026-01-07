package com.chung.taskcrud.auth.service.impl;

import com.chung.taskcrud.auth.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Override
    public void sendVerifyEmail(String to, String verifyLink) {
        log.info("VERIFY EMAIL => to={} link={}", to, verifyLink);
    }
}