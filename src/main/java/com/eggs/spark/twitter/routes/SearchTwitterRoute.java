package com.eggs.spark.twitter.routes;

import com.eggs.spark.twitter.domain.Incident;
import com.eggs.spark.twitter.domain.Tweets;
import com.eggs.spark.twitter.services.InvalidRequestService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jon on 13/02/2017.
 */
@Component("searchTwitterRoute")
public class SearchTwitterRoute extends RouteBuilder {

    private final Processor incidentProcessor;
    private final Processor responseProcessor;

    @Autowired
    private SearchTwitterRoute(@Qualifier("incidentProcessor") final Processor incidentProcessor,
                               @Qualifier("responseProcessor") final Processor responseProcessor) {
        this.incidentProcessor = incidentProcessor;
        this.responseProcessor = responseProcessor;

    }

    @Override
    public void configure() throws Exception {

        onException(TwitterException.class).process(incidentProcessor)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json")).handled(true);

        onException(ValidationException.class).bean(new InvalidRequestService(), "missingParameters")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.UNPROCESSABLE_ENTITY_422))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json")).handled(true);

        /* Need a catch all exception class*/
        onException(Throwable.class).process(incidentProcessor)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.INTERNAL_SERVER_ERROR_500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json")).handled(true);

        restConfiguration()
                .component("spark-rest")
                .contextPath("/twitter-proxy-service")
                .skipBindingOnErrorCode(false)
                .apiContextPath("api-doc")
                .apiProperty("api.title", "Twitter Proxy Service")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api")
                .bindingMode(RestBindingMode.json)
                .port(8080)
                .dataFormatProperty("prettyPrint", "true")
                .dataFormatProperty("json.in.disableFeatures", "WRITE_DATES_AS_TIMESTAMPS")
                .dataFormatProperty("json.out.disableFeatures", "WRITE_DATES_AS_TIMESTAMPS")
                .dataFormatProperty("json.in.moduleRefs", "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
                .dataFormatProperty("json.out.moduleRefs", "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
                .dataFormatProperty("json.in.moduleRefs", "com.fasterxml.jackson.datatype.guava.GuavaModule")
                .dataFormatProperty("json.out.moduleRefs", "com.fasterxml.jackson.datatype.guava.GuavaModule");

        rest("/twitter/search")
                .consumes("application/json")
                .produces("application/json")
                .get()
                    .responseMessage()
                        .code(HttpStatus.OK_200)
                        .responseModel(Tweets.class)
                        .message("success")
                    .endResponseMessage()
                    .responseMessage()
                        .code(HttpStatus.BAD_GATEWAY_502)
                        .responseModel(Incident.class)
                        .message("error")
                    .endResponseMessage()
                    .responseMessage()
                        .code(HttpStatus.UNPROCESSABLE_ENTITY_422)
                        .responseModel(Incident.class)
                        .message("invalid request")
                    .endResponseMessage()
                    .responseMessage()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR_500)
                        .responseModel(Incident.class)
                        .message("Unexpected exception")
                    .endResponseMessage()
                    .route()
                        .routeId("TwitterSearchRoute")
                        .validate(header("keywords").isNotNull())
                        .validate(header("keywords").convertToString().isNotEqualTo(""))
                        .process( exchange -> {
                            StringBuilder builder = new StringBuilder();
                            String kywrds = exchange.getProperty("keywords", String.class);
                            List<String> keywords = Arrays.asList(kywrds.split(","));
                            Iterator<String> it = keywords.iterator();
                            while (it.hasNext()) {
                                String keyword = it.next();
                                if (!keyword.startsWith("#")) {
                                    builder.append("#");
                                }
                                builder.append(keyword);
                                if (it.hasNext()) {
                                    builder.append(" OR ");
                                }
                            }
                            exchange.getIn().setHeader("CamelTwitterKeywords", builder.toString());
                            exchange.getIn().setHeader("CamelTwitterSearchLanguage", "en");
                            exchange.getIn().setBody(builder.toString());
                        })
                        .id("Create Request").description("Format and generate the request for Twitter")
                        .to("twitter://search").id("InvokeTwitter").description("Search Twitter")
                        .process( responseProcessor ).id("ResponseProcessor").description("Twitter Response Processor")
                .end();



    }
}
