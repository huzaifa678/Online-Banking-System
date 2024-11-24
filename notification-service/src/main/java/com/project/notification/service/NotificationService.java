package com.project.notification.service;


import com.project.account.event.AccountClosedEvent;
import com.project.account.event.AccountCreatedEvent;
import com.project.account.event.AccountUpdatedEvent;
import com.project.payment.event.PaymentCreatedEvent;
import com.project.transaction.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "account-created")
    public void listenAccountCreated(AccountCreatedEvent accountCreatedEvent) {
        log.info("Got the message from the account-created topic {}", accountCreatedEvent);
        sendEmail(accountCreatedEvent.getUserEmail().toString(), "Account Created",
                String.format("Your account with Account ID %s has been created successfully.", accountCreatedEvent.getAccountId()));
    }


    @KafkaListener(topics = "account-updated")
    public void listenAccountUpdated(AccountUpdatedEvent accountUpdatedEvent) {
        log.info("Got the message from the account-updated topic {}", accountUpdatedEvent);
        sendEmail(accountUpdatedEvent.getUserEmail().toString(), "Account Updated",
                String.format("Your account with Account ID %s has been updated.", accountUpdatedEvent.getAccountId()));
    }

    @KafkaListener(topics = "account-closed")
    public void listenAccountClosed(AccountClosedEvent accountClosedEvent) {
        log.info("Got the message from the account-closed topic {}", accountClosedEvent);
        sendEmail(accountClosedEvent.getUserEmail().toString(), "Account Closed",
                String.format("Your account with Account ID %s has been closed.", accountClosedEvent.getAccountId()));
    }


    private void sendEmail(String toEmail, String subject, String body) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("bankingsystem@email.com");
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(String.format("""
                            Hi %s,

                            %s
                            
                            Best Regards,
                            Banking system manager
                            """, toEmail, body));
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("{} notification email sent to {}", subject, toEmail);
        } catch (MailException e) {
            log.error("Exception occurred when sending mail to {}", toEmail, e);
            throw new RuntimeException("Exception occurred when sending mail", e);
        }
    }

    @KafkaListener(topics = "transaction-created")
    public void listenTransactionCreated(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("Got the message from transaction-created topic {}", transactionCreatedEvent);

        String transactionStatus = transactionCreatedEvent.getStatus().toString();

        if ("COMPLETED".equalsIgnoreCase(transactionStatus)) {
            log.info("Transaction {} completed successfully.", transactionCreatedEvent.getStatus());
        } else if ("FAILED".equalsIgnoreCase(transactionStatus)) {
            log.warn("Transaction {} failed.", transactionCreatedEvent.getStatus());
        } else {
            log.info("Transaction {} has a status: {}", transactionCreatedEvent.getStatus(), transactionStatus);
        }
    }

    @KafkaListener(topics = "payment-created")
    public void listenPaymentCreated(PaymentCreatedEvent paymentCreatedEvent) {
        log.info("Got the message from payment-created topic {}", paymentCreatedEvent);

        String transactionStatus = paymentCreatedEvent.getStatus().toString();

        if ("COMPLETED".equalsIgnoreCase(transactionStatus)) {
            log.info("Payment {} completed successfully.", paymentCreatedEvent.getStatus().toString());
        } else if ("FAILED".equalsIgnoreCase(transactionStatus)) {
            log.warn("Payment {} failed.", paymentCreatedEvent.getStatus());
        } else {
            log.info("Payment {} has a status: {}", paymentCreatedEvent.getStatus(), transactionStatus);
        }
    }
}
