package com.involves.springbatchstudy.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DatabaseConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun springBatchDatasource(): DataSource =
        DataSourceBuilder.create().build()

    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    fun appDatasource(): DataSource =
        DataSourceBuilder.create().build()

    @Bean
    fun transactionalManagerApp(@Qualifier("appDatasource") dataSource: DataSource): PlatformTransactionManager =
        DataSourceTransactionManager(dataSource)

}