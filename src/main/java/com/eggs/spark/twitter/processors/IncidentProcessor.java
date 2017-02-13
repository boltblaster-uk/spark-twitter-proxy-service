package com.eggs.spark.twitter.processors;

import com.eggs.spark.twitter.domain.Incident;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created by Jon on 13/02/2017.
 */
@Component("incidentProcessor")
public class IncidentProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {

        Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);

        String incidentId = UUID.randomUUID().toString();
        Incident incident = new Incident(incidentId, caused.getMessage());




        String incidentMsg =
                String.format("Incident Id = %s : Raised on = %s\n : Stack Trace = %s", incidentId,
                        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSSSSS Z")),
                        ExceptionUtils.getStackTrace(caused));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(incident);
        exchange.getOut().setBody(json);
    }
}