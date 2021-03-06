package com.huorong.utils;

import com.huorong.domain.AdminEmail;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailUtil {
    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);
    private static final String HOST = "smtp.163.com";
    private static final String PROTOCOL = "smtp";
    private static final String PORT = "465";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    /**
     * 获取Session
     *
     * @return session
     */
    private static Session getSession(AdminEmail adminEmail, boolean isBuild) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        if (!isBuild) {
            props.put("mail.smtp.host", HOST);// 设置服务器地址
            props.put("mail.smtp.port", PORT);// 设置端口
            props.put("mail.store.protocol", PROTOCOL);// 设置协议
            log.debug("测试");
            log.info("测试");
        } else {
            props.setProperty("mail.smtp.host", HOST);
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", PORT);
            props.setProperty("mail.smtp.socketFactory.port", PORT);
            log.debug("生产");
            log.info("生产");
        }
        return Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(adminEmail.getName(), adminEmail.getAuthPassword());
            }

        });
    }

    public static boolean sendEmail(String toEmail, String content, AdminEmail adminEmail, boolean isBuild) {
        Session session = getSession(adminEmail, isBuild);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(adminEmail.getName()));
            InternetAddress[] address = { new InternetAddress(toEmail) };
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("账号激活邮件");
            msg.setSentDate(new Date());
            msg.setContent("<div align=\"center\">" + content + "</div>", "text/html;charset=utf-8");
            msg.saveChanges();
            Transport.send(msg);
            log.info("admin发送激活邮件给{},内容{}", toEmail, content);
            log.debug("admin发送激活邮件给{},内容{}", toEmail, content);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
        return true;
    }

    public static void sendEmailAsyn(String toEmail, String msg, AdminEmail adminEmail, boolean isBuild) {
        Thread t = new Thread(() -> sendEmail(toEmail, msg, adminEmail, isBuild));
        t.start();
    }

    /**
     * HTML email
     * 
     * @param emailNO
     *            收件人邮箱
     * @param emailName
     *            邮件名字
     * @param emailText
     *            邮件内容
     */
    public static void sendHtmlEmail(String emailNO, String emailName, String emailText, AdminEmail adminEmail) {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(HOST);
        email.setAuthentication(adminEmail.getName(), adminEmail.getAuthPassword());
        email.setSubject(emailName);
        email.setCharset("UTF-8");
        try {
            email.setFrom(adminEmail.getName(), "blogAdmin");
            email.addTo(emailNO);
            email.setTextMsg(emailText);
            email.send();
        } catch (Exception e) {
            log.error("email send error", e);
            e.printStackTrace();
        }
    }

}
