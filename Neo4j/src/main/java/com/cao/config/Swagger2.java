package com.cao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.cao.controller")).paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		StringBuilder desStr = new StringBuilder();
		desStr.append("以下是针对接口中的公共组件用法的使用说明：<br>");
		desStr.append(
				"0、错误规则，0000:操作成功；9999:服务器异常；9000:登录超时；9001:操作失败；9002:数据不存在；9003:验证码错误；9004:用户名密码错误；9005:短信发送失败；<br>");
		return new ApiInfoBuilder().title("接口文档").description(desStr.toString()).version("1.0.0")
				.termsOfServiceUrl("http://www.project.com").license("LICENSE").licenseUrl("http://www.project.com")
				.build();
	}
}