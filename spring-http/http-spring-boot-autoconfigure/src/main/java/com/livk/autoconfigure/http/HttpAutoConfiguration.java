package com.livk.autoconfigure.http;

import com.livk.autoconfigure.http.factory.HttpServiceRegistrar;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * <p>
 * HttpAutoConfiguration
 * </p>
 *
 * @author livk
 * @date 2022/5/20
 */
@AutoConfiguration
@ConditionalOnClass(WebClient.class)
public class HttpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient() {
        return WebClient.builder().build();
    }


    @Bean
    public HttpServiceRegistrar httpServiceRegistrar(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build();
        return new HttpServiceRegistrar(httpServiceProxyFactory);
    }
}
