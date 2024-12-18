package com.team1206.pos.sns;

import com.team1206.pos.exceptions.SnsServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

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
                System.out.println("Message sent with ID: " + response.messageId());
            }
        } catch (SnsException snsException) {
            // Extract details from the AWS exception
            System.err.println("AWS SNS Error: " + snsException.awsErrorDetails().errorMessage());
            System.err.println("Error Code: " + snsException.awsErrorDetails().errorCode());
            System.err.println("Service Name: " + snsException.awsErrorDetails().serviceName());

            throw new SnsServiceException("Failed to send SMS via AWS SNS", snsException);
        } catch (Exception e) {
            System.err.println("Unexpected error while sending SMS: " + e.getMessage());
            throw new SnsServiceException("An unexpected error occurred", e);
        }
    }
}
