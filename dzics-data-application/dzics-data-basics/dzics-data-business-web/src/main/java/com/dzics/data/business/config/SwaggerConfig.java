package com.dzics.data.business.config;

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
                .groupName("A系统管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.sys"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "BreateRestApi")
    public Docket breateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("B生产数据")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.base.pdm"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "CreateRestApi")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("C文件管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.file"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "DreateRestApi")
    public Docket dreateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("D订单产线管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.odline"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "EreateRestApi")
    public Docket ereateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("E产品管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.pms"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "FreateRestApi")
    public Docket freateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("F作业管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.zkms"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "GreateRestApi")
    public Docket greateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("G设备管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.equipment"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "HreateRestApi")
    public Docket hreateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("H机床数据")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.machinetool"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "IreateRestApi")
    public Docket ireateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("J维护管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.maintain"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "JreateRestApi")
    public Docket robotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("I机器人数据")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.robot"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "KreateRestApi")
    public Docket KobotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("K工序管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.workProcedure"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "LreateRestApi")
    public Docket LobotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("L工位管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.workStation"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "MreateRestApi")
    public Docket MobotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("M告警配置")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.alarm"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "NreateRestApi")
    public Docket NobotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("N刀具配置")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.tool"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "pobotRestApi")
    public Docket pobotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("P日志管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.logger"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "qobotRestApi")
    public Docket qobotRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("Q公共管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.common"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "appointApi")
    public Docket appointApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("L定制服务")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.appoint"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "mppointApi")
    public Docket mppointApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("M Excel导出管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.excel"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "oppointApi")
    public Docket oppointApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("O 首页接口")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.index"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "pppointApi")
    public Docket pppointApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("P 产能管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.data.business.controller.productionplan"))
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
                .contact(new Contact("大正数据采集api", "http://127.0.0.1:8082/swagger-ui.html", "xxxxxxx@xx.com"))
                // 定义版本号
                .version("1.0")
                .build();
    }

}
