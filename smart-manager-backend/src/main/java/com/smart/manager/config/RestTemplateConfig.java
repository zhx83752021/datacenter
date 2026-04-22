package com.smart.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 * 用于系统发起对外 HTTP 拉取请求 (主要服务于 ETL 以及 钉钉/企微 推送)
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 设置连接超时 10 秒
        requestFactory.setConnectTimeout(10000);
        // 设置读取延时 30 秒 (对于有些报表聚合类接口可能需要跑很久)
        requestFactory.setReadTimeout(30000);
        return new RestTemplate(requestFactory);
    }
}
