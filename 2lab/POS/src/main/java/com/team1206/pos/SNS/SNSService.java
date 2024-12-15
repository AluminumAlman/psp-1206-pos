package com.team1206.pos.SNS;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

public class SNSService {


    public void sendSms(String phoneNumber, String message) {
        try (SnsClient snsClient = SnsClient.create()) {
            // Create a PublishRequest
            PublishRequest request = PublishRequest.builder()
                    .message(message)                 // Message body
                    .phoneNumber(phoneNumber)         // Phone number (in E.164 format, e.g., "+15551234567")
                    .build();

            // Publish the message
            PublishResponse response = snsClient.publish(request);
            System.out.println("Message sent with ID: " + response.messageId());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}
