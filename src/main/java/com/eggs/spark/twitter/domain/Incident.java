package com.eggs.spark.twitter.domain;

import lombok.Value;

/**
 * Created by Jon on 13/02/2017.
 */
@Value
public class Incident {
    private final String incidentId;
    private final String message;
}
