package com.eggs.spark.twitter;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.CamelContext;
import org.apache.camel.component.twitter.TwitterComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.javaconfig.Main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;

/**
 * Created by Jon on 13/02/2017.
 */
@Configuration
@ComponentScan("com.eggs.spark.twitter")
public class TwitterProxyService  extends CamelConfiguration {
    @Value("${twitter.consumerKey}")
    private String consumerKey;

    @Value("${twitter.consumerSecret}")
    private String consumerSecret;

    @Value("${twitter.accessToken}")
    private String accessToken;

    @Value("${twitter.accessTokenSecret}")
    private String accessTokenSecret;

    public static void main(String[] args) throws Exception{
        Main main = new Main();
        main.setConfigClass(TwitterProxyService.class);
        main.run();
    }

    @Bean(name = "com.fasterxml.jackson.datatype.guava.GuavaModule")
    Module guavaModule() {
        return new GuavaModule();
    }

    @Bean(name = "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
    Module javaTimeModule() {
        return new JavaTimeModule();
    }

        @Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
        camelContext.addComponent("twitter", getTwitterComponent());
//        camelContext.addComponent("com.fasterxml.jackson.datatype.guava.GuavaModule", new GuavaModule());
//        camelContext.addComponent("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", new JavaTimeModule());
    }

    private TwitterComponent getTwitterComponent() {
        TwitterComponent twitterComponent = new TwitterComponent();
        twitterComponent.setAccessToken(accessToken);
        twitterComponent.setAccessTokenSecret(accessTokenSecret);
        twitterComponent.setConsumerKey(consumerKey);
        twitterComponent.setConsumerSecret(consumerSecret);
        return twitterComponent;
    }
}
