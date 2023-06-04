package com.involves.springbatchstudy.step

import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.function.FunctionItemProcessor
import org.springframework.batch.item.support.IteratorItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class EvenOrOddStepConfig {

    @Bean
    fun stepEvenOrOdd(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step =
        StepBuilder("printEvenOrOdd", jobRepository)
            .chunk<Int, String>(1, transactionManager)
            .reader(readUntilTen())
            .processor(processorEvenOrOdd())
            .writer(printNumber())
            .build()

    fun readUntilTen(): ItemReader<Int> = IteratorItemReader(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).iterator())

    fun processorEvenOrOdd(): ItemProcessor<Int, String> = FunctionItemProcessor {
        if (it % 2 == 0) {
            return@FunctionItemProcessor "Item is even $it"
        } else {
            return@FunctionItemProcessor "Item is odd $it"
        }
    }

    fun printNumber(): ItemWriter<String> = ItemWriter { itens ->
        itens.forEach { println(it) }
    }
}