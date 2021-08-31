package com.example.snslogin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@MapperScan("com.example.snslogin.mapper")
@Configuration
public class DatabaseConfig {

    public class DatabaseConfiguration {
        @Bean
        public DataSourceTransactionManager mybatisTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }
}
