package com.dzics.data.appoint.changsha.mom.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@EnableKnife4j
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean enableSwagger;

    @Bean(name = "AreateRestApi")
    public Docket areateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("A mom管理系统")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.appoint.changsha.mom.controller"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);


    }

    @Bean(name = "BreateRestApi")
    public Docket breateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("B 数据处理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.appoint.changsha.mom.hardware.controller"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);


    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("大正数据采集api接口文档")
                // 描述
                .description("欢迎使用大正数据采集api接口文档")
                // 设置联系人
                .contact(new Contact("大正数据采集api", "http://127.0.0.1:8127/swagger-ui.html", "xxxxxxx@xx.com"))
                // 定义版本号
                .version("1.0")
                .build();
    }

}
