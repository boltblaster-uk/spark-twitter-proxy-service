package com.eggs.spark.twitter.processors;

import com.eggs.spark.twitter.domain.Tweet;
import com.eggs.spark.twitter.domain.Tweets;
import com.eggs.spark.twitter.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jon on 13/02/2017.
 */
@Slf4j
@Component("responseProcessor")
public class ResponseProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        List<Status> statuses = exchange.getIn().getBody(List.class);
        if (CollectionUtils.isEmpty(statuses)) {
            Tweets tweets = new Tweets(new ArrayList<>());
            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            exchange.getIn().setBody(tweets);
        } else {
            List<Tweet> tweetList = new ArrayList<>();
            statuses.forEach( status -> {
                tweetList.add( buildTweet(status));
            });

            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            exchange.getIn().setBody(new Tweets(tweetList));
        }
    }

    private Tweet buildTweet(Status status) {
        return new Tweet(status.getId(), status.getText(), getUser(status.getUser()),
                status.getRetweetCount(), status.getFavoriteCount(),
                getHashtags(status.getHashtagEntities()));
    }

    /**
     * Map a twitter4j user to our user.
     * @param user a twitter4j user.
     * @return our user.
     */
    private User getUser(final twitter4j.User user) {
        return new User(user.getId(), user.getName(), user.getScreenName(), user.getFollowersCount(),
                user.getLocation());
    }

    /**
     * Extract the hash tags from the tweet.
     * @param hashtags an array HashtagEntities.
     * @return a list of the hashtag text values.
     */
    private List<String> getHashtags(final HashtagEntity[] hashtags) {
        List<String> hTags = new ArrayList<>();
        for (HashtagEntity entity : hashtags) {
            hTags.add(entity.getText());
        }
        return hTags;
    }
}
