package com.pratilipi.gameconnect4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@ComponentScan(basePackages="com.pratilipi")
public class Gameconnect4Application {

	public static void main(String[] args) {
		SpringApplication.run(Gameconnect4Application.class, args);
	}

}
