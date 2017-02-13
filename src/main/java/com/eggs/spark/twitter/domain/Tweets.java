package com.eggs.spark.twitter.domain;

import lombok.Value;

import java.util.List;

/**
 * Created by Jon on 13/02/2017.
 */
@Value
public class Tweets {
    private final List<Tweet> tweets;
}
