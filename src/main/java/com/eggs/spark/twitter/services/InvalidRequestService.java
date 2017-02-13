package com.eggs.spark.twitter.services;

import com.eggs.spark.twitter.domain.Incident;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import spark.utils.StringUtils;

import java.util.UUID;

/**
 * Created by Jon on 13/02/2017.
 */
@Slf4j
public class InvalidRequestService {

    public void missingParameters(Exchange exchange) throws Exception {
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
        ObjectMapper mapper = new ObjectMapper();
        Incident incident = new Incident(UUID.randomUUID().toString(), "Missing Keywords");
        exchange.getOut().setBody(mapper.writeValueAsString(incident));
    }
}
