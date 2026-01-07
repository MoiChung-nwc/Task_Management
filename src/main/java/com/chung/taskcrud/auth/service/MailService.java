package com.chung.taskcrud.auth.service;

public interface MailService {
    void sendVerifyEmail(String to, String verifyLink);
}
