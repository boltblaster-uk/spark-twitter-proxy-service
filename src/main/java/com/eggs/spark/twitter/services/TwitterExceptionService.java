package com.eggs.spark.twitter.services;

import com.eggs.spark.twitter.domain.Incident;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created by Jon on 13/02/2017.
 */
public class TwitterExceptionService {
    public void twitterException(Exchange exchange) {
        Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        String incidentId = UUID.randomUUID().toString();
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSSSSS Z");


        StringBuilder builder = new StringBuilder();
        builder.append("Incident ").append(incidentId).append(" raised on ")
                .append(ZonedDateTime.now().format(FORMATTER)).append(".\n")
                .append(ExceptionUtils.getStackTrace(caused));


        Incident incident = new Incident(incidentId.toString(), caused.getMessage());
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 502);
        exchange.getIn().setBody(incident);
    }
}
