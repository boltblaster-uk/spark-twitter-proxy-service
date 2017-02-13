package com.eggs.spark.twitter.domain;

import lombok.Value;

/**
 * Created by Jon on 13/02/2017.
 */
@Value
public class User {
    private final Long id;
    private final String name;
    private final String screenName;
    private final Integer followerCount;
    private final String location;
}
