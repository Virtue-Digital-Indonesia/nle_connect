package com.nle.config.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource(C3P0DataSourceProperties dataSourcePros) throws PropertyVetoException {

        ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();

        pooledDataSource.setDriverClass("com.nle.config.c3p0.C3P0DataSourceProperties");
        pooledDataSource.setUser("root");
        pooledDataSource.setPassword("TXPSfYUiX9C4MmocXX0O");
        pooledDataSource.setJdbcUrl("jdbc:mysql://210.247.245.143:3308/nlebackend?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true");
        pooledDataSource.setMinPoolSize(5);
        pooledDataSource.setMaxPoolSize(20);
        pooledDataSource.setMaxIdleTime(1800);

        return pooledDataSource;
    }
}
