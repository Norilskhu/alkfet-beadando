package zp.gde.hu.alkfetsvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.db-svc.base-url}")
    private String dbSvcBaseUrl;

    @Bean
    public RestClient dbSvcRestClient() {
        return RestClient.builder()
                .baseUrl(dbSvcBaseUrl)
                .build();
    }
}

