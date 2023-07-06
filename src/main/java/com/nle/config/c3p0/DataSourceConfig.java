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

        pooledDataSource.setDriverClass(dataSourcePros.getDriverClass());
        pooledDataSource.setUser(dataSourcePros.getUsername());
        pooledDataSource.setPassword(dataSourcePros.getPassword());
        pooledDataSource.setJdbcUrl(dataSourcePros.getUrl());
        pooledDataSource.setInitialPoolSize(dataSourcePros.getInitialPoolSize());
        pooledDataSource.setMinPoolSize(dataSourcePros.getMinPoolSize());
        pooledDataSource.setMaxPoolSize(dataSourcePros.getMaxPoolSize());
        pooledDataSource.setAcquireIncrement(dataSourcePros.getAcquireIncrement());
        pooledDataSource.setCheckoutTimeout(dataSourcePros.getTimeout());

        return pooledDataSource;
    }
}
