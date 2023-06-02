package com.involves.springbatchstudy

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class BatchConfig {

    /*
    * Each Job is executed only
    * For execute many times add incrementer
    *
    * use @Value("#{jobParameters['name']}" to get runtime parameters
    **/

    @Bean
    fun sayHello(jobRepository: JobRepository, step: Step): Job =
        JobBuilder("sayHello", jobRepository)
            .start(step)
            .incrementer(RunIdIncrementer())
            .build()

    @Bean
    fun step(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step =
        StepBuilder("hello", jobRepository)
            .tasklet(printHello(null), transactionManager)
            .build()

    @Bean
    @StepScope
    fun printHello(@Value("#{jobParameters['name']}") name: String?): Tasklet {
        return Tasklet { _, _ ->
            println("Hi $name")
            RepeatStatus.FINISHED
        }
    }
}
