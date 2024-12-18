package com.team1206.pos.SNS;

import com.team1206.pos.exceptions.SnsServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@Service
public class SNSService {
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    public void sendSms(String phoneNumber, String message) {
        try (SnsClient snsClient = SnsClient.create()) {
            // Create a PublishRequest
            PublishRequest request = PublishRequest.builder()
                    .message(message)                 // Message body
                    .phoneNumber(phoneNumber)         // Phone number (in E.164 format, e.g., "+15551234567")
                    .build();

            // Publish the message
            if (activeProfile.equals("prod")) {
                PublishResponse response = snsClient.publish(request);
                log.info("Message sent with ID: {}", response.messageId());
            }
        } catch (SnsException snsException) {
            // Extract details from the AWS exception
            log.error("AWS SNS Error: {}\nAWS SNS Error Code: {}\nAWS SNS Service Name: {}",
                    snsException.awsErrorDetails().errorMessage(), snsException.awsErrorDetails().errorCode(), snsException.awsErrorDetails().serviceName());

            throw new SnsServiceException("Failed to send SMS via AWS SNS", snsException);
        } catch (Exception e) {
            log.error("Unexpected error while sending SMS: {}", e.toString());
            throw new SnsServiceException("An unexpected error occurred", e);
        }
    }
}
