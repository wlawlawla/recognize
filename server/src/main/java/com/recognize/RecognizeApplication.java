package com.recognize;

import com.recognize.user.config.JWTConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JWTConfig.class})
@SpringBootApplication
@MapperScan(
        basePackages = {"com.**.mapper"}
)
public class RecognizeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecognizeApplication.class, args);

    }

}
