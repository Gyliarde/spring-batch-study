package com.involves.springbatchstudy.job

import com.involves.springbatchstudy.model.Client
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.ItemPreparedStatementSetter
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


@Configuration
class DatabaseReadConfig {

    @Bean
    fun jobDatabaseReader(jobRepository: JobRepository, stepDatabaseReader: Step): Job =
        JobBuilder("jobCursorReader", jobRepository)
            .start(stepDatabaseReader)
            .incrementer(RunIdIncrementer())
            .build()

    @Bean
    fun stepDatabaseReader(
        jobRepository: JobRepository,
        @Qualifier("transactionalManagerApp") transactionManager: PlatformTransactionManager,
        readerJdbcPaginator: ItemReader<Client>,
        jdbcProfessionalItemWriter: ItemWriter<Client>
    ): Step =
        StepBuilder("stepCursorReader", jobRepository)
            .chunk<Client, Client>(1, transactionManager)
            .reader(readerJdbcPaginator)
            .writer(jdbcProfessionalItemWriter)
            .build()

    @Bean
    fun readerJdbcCursor(@Qualifier("appDatasource") dataSource: DataSource): JdbcCursorItemReader<Client> {
        return JdbcCursorItemReaderBuilder<Client>()
            .name("jdbcCursorReader")
            .dataSource(dataSource)
            .sql("select * from cliente")
            .rowMapper(DataClassRowMapper(Client::class.java))
            .build()
    }

    @Bean
    fun readerJdbcPaginator(
        @Qualifier("appDatasource") dataSource: DataSource,
        queryProvider: PagingQueryProvider
    ): JdbcPagingItemReader<Client> {
        return JdbcPagingItemReaderBuilder<Client>()
            .name("jdbcPaginatorReader")
            .dataSource(dataSource)
            .queryProvider(queryProvider)
            .pageSize(1)
            .rowMapper(DataClassRowMapper(Client::class.java))
            .build()
    }

    @Bean
    fun queryProvider(@Qualifier("appDatasource") dataSource: DataSource): SqlPagingQueryProviderFactoryBean {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("select * ")
        queryProvider.setFromClause("from cliente")
        queryProvider.setSortKey("email")
        return queryProvider
    }

    @Bean
    fun writerJdcCursor(): ItemWriter<Client> = ItemWriter { client ->
        client.forEach { println(client) }
    }

    @Bean
    fun jdbcProfessionalItemWriter(@Qualifier("appDatasource") dataSource: DataSource) : JdbcBatchItemWriter<Client> =
         JdbcBatchItemWriterBuilder<Client>()
            .dataSource(dataSource)
            .sql(
                "INSERT INTO fornecedores (nome, sobrenome, idade, email, ean) VALUES(?, ?, ?, ?, ?)"
            )
            .itemPreparedStatementSetter(itemPreparedStatementSetter())
            .build()

    private fun itemPreparedStatementSetter() : ItemPreparedStatementSetter<Client> {
        return ItemPreparedStatementSetter<Client> { item, ps ->
            ps.setString(1, item.nome)
            ps.setString(2, item.sobrenome)
            ps.setString(3, item.idade)
            ps.setString(4, item.email)
            ps.setString(5, "12345678")
        }
    }
}