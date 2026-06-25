package com.afformed.vehicle_scheduler.config;

import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, AffordmedApiProperties apiProperties) {
        return restTemplateBuilder
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    requestFactory.setConnectTimeout(apiProperties.getConnectTimeoutMillis());
                    requestFactory.setReadTimeout(apiProperties.getReadTimeoutMillis());
                    return requestFactory;
                })
                .additionalInterceptors((request, body, execution) -> {
                    if (StringUtils.hasText(apiProperties.getBearerToken())) {
                        request.getHeaders().setBearerAuth(apiProperties.getBearerToken());
                    }
                    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
                    return execution.execute(request, body);
                })
                .build();
    }
}
