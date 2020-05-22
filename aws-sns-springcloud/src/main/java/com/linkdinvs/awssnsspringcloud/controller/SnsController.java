package com.linkdinvs.awssnsspringcloud.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.linkdinvs.awssnsspringcloud.util.AWSUtil;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

@RestController
public class SnsController {
	
	 	@Autowired
	    NotificationMessagingTemplate notificationMessagingTemplate;
	 
		@Autowired
	    AWSUtil awsUtil;

	    @RequestMapping("/createTopic")
	    private String createTopic(@RequestParam("topic_name") String topicName) throws URISyntaxException {
	        //topic name cannot contain spaces
	        final CreateTopicRequest topicCreateRequest = CreateTopicRequest.builder().name(topicName).build();

	        SnsClient snsClient = awsUtil.getSnsClient();

	        final CreateTopicResponse topicCreateResponse = snsClient.createTopic(topicCreateRequest);

	        if (topicCreateResponse.sdkHttpResponse().isSuccessful()) {
	            System.out.println("Topic creation successful");
	            System.out.println("Topics: " + snsClient.listTopics());
	        } else {
	            throw new ResponseStatusException(
	                    HttpStatus.INTERNAL_SERVER_ERROR, topicCreateResponse.sdkHttpResponse().statusText().get()
	            );
	        }

	        snsClient.close();
	        return "Topic ARN: " + topicCreateResponse.topicArn();
	    }


	    @RequestMapping("/addSubscribers")
	    private String addSubscriberToTopic(@RequestParam("arn") String arn, @RequestParam("emailOrPhone") String emailOrPhone, 
	    		 @RequestParam("protocol") String protocol) throws URISyntaxException {

	        SnsClient snsClient = awsUtil.getSnsClient();

	        final SubscribeRequest subscribeRequest = SubscribeRequest.builder()
	                .topicArn(arn)
	                .protocol(protocol)
	                .endpoint(emailOrPhone)
	                .build();

	        SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);

	        if (subscribeResponse.sdkHttpResponse().isSuccessful()) {
	            System.out.println("Subscriber creation successful");
	        } else {
	            throw new ResponseStatusException(
	                    HttpStatus.INTERNAL_SERVER_ERROR, subscribeResponse.sdkHttpResponse().statusText().get()
	            );
	        }

	        snsClient.close();

	        return "Subscription ARN request is pending. To confirm the subscription, check your email.";
	    }
	    
	    @RequestMapping("/sendEmail")
	    private String sendEmail(@RequestParam("arn") String arn) throws URISyntaxException {

	        SnsClient snsClient = awsUtil.getSnsClient();

	        final String msg = "Email works!";

	        final PublishRequest publishRequest = PublishRequest.builder()
	                                              .topicArn(arn)
	                                              .subject("SNS EMAIL AWS OK")
	                                              .message(msg)
	                                              .build();

	        PublishResponse publishResponse = snsClient.publish(publishRequest);

	        if (publishResponse.sdkHttpResponse().isSuccessful()) {
	            System.out.println("Message publishing successful");
	        } else {
	            throw new ResponseStatusException(
	                HttpStatus.INTERNAL_SERVER_ERROR, publishResponse.sdkHttpResponse().statusText().get());
	        }

	        snsClient.close();
	        return "Email sent to subscribers. Message-ID: " + publishResponse.messageId();
	    }

	    @RequestMapping("/sendSMS")
	    private String sendBulkSMS(@RequestParam("arn") String arn) throws URISyntaxException {

	        SnsClient snsClient = awsUtil.getSnsClient();

	        final PublishRequest publishRequest = PublishRequest.builder()
	                                              .topicArn(arn)
	                                              .message("Deu certo galera!")
	                                              .build();

	        PublishResponse publishResponse = snsClient.publish(publishRequest);

	        if (publishResponse.sdkHttpResponse().isSuccessful()) {
	            System.out.println("Bulk Message sending successful");
	            System.out.println(publishResponse.messageId());
	        } else {
	            throw new ResponseStatusException(
	                HttpStatus.INTERNAL_SERVER_ERROR, publishResponse.sdkHttpResponse().statusText().get()
	            );
	        }
	        snsClient.close();
	        return "Done";
	    }
}
