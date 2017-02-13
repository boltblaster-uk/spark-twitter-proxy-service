package com.eggs.spark.twitter.domain;

import lombok.Value;

import java.util.List;

/**
 * Created by Jon on 13/02/2017.
 */
@Value
public class Tweet {
    private final Long id;
    private final String tweet;
    private final User user;
    private final Integer retweets;
    private final Integer favourites;
    private final List<String> hashtags;
}
