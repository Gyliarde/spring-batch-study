package com.involves.springbatchstudy.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class EvenOrOddBatchConfig {

    @Bean
    fun evenOrOdd(jobRepository: JobRepository, stepEvenOrOdd: Step): Job =
        JobBuilder("evenOrOdd", jobRepository)
            .start(stepEvenOrOdd)
            .incrementer(RunIdIncrementer())
            .build()
}