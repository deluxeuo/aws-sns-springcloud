package com.linkdinvs.awssnsspringcloud.util;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Component
public class AWSUtil {

	@Autowired
	private BasicAWSCredentials basicAWSCredentials;
	
    public static AwsCredentialsProvider getAwsCredentials(String accessKeyID, String secretAccessKey) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyID, secretAccessKey);

        AwsCredentialsProvider awsCredentialsProvider = () -> awsBasicCredentials;

        return awsCredentialsProvider;
    }


    /**
     * Returns an SnsClient Object that can be used to perform all AWS SNS operations
     *
     * @return
     * @throws URISyntaxException
     */
    public SnsClient getSnsClient() throws URISyntaxException {
        return SnsClient.builder()
                .credentialsProvider(getAwsCredentials(
                		basicAWSCredentials.getAWSAccessKeyId(),
                		basicAWSCredentials.getAWSSecretKey()))
                .region(Region.US_EAST_1) //Set your selected region
                .build();
    }
}
