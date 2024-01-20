package org.studyeasy.SpringBlog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class AppConfig {

    @Value("${mail.transport.protocol}")
    private String mail_transport_protocol;

    @Value("${spring.mail.host}")
    private String spring_mail_host;

    @Value("${spring.mail.port}")
    private String spring_mail_port;

    @Value("${spring.mail.username}")
    private String spring_mail_username;

    @Value("${spring.mail.password}")
    private String spring_mail_password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String spring_mail_properties_mail_smtp_auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String spring_mail_properties_mail_smtp_starttls_enable;

    @Value("${spring.mail.smtp.ssl.trust}")
    private String spring_mail_smtp_ssl_trust;

    @Bean
    public JavaMailSender getJavaMailSender() {
        final var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(spring_mail_host);
        mailSender.setPort(Integer.parseInt(spring_mail_port));

        mailSender.setUsername(spring_mail_username);
        mailSender.setPassword(spring_mail_password);

        final var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mail_transport_protocol);
        props.put("mail.smtp.auth", spring_mail_properties_mail_smtp_auth);
        props.put("mail.smtp.starttls.enable", spring_mail_properties_mail_smtp_starttls_enable);
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", spring_mail_smtp_ssl_trust);

        return mailSender;
    }
}
