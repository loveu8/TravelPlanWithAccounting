package com.travelPlanWithAccounting.service.config;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class MailConfig {

  @Value("${spring.mail.host:}")
  private String host;

  @Value("${spring.mail.port:}")
  private int port;

  @Value("${spring.mail.username:}")
  private String username;

  @Value("${spring.mail.password:}")
  private String password;

  /**
   * 配置 JavaMailSender bean，使用 SMTP 協議發送郵件。<br>
   * Configures a JavaMailSender bean to send emails using SMTP protocol.
   *
   * @return JavaMailSender 實例 (JavaMailSender instance)
   */
  @Bean
  public JavaMailSender javaMailSender() {
    if (!StringUtils.hasText(host)
        || !StringUtils.hasText(username)
        || !StringUtils.hasText(password)) {
      log.error("[MailConfig] Mail config not set, mail sender will be disabled.");
      return new JavaMailSenderImpl(); // dummy sender, send() 會失敗但不影響啟動
    }
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.trust", host);
    props.put("mail.debug", "true");
    props.put("mail.smtp.timeout", "20000");
    props.put("mail.smtp.writetimeout", "20000");
    props.put("mail.smtp.connectiontimeout", "20000");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

    return mailSender;
  }
}
