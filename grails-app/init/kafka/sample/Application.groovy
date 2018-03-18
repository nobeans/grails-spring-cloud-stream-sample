package kafka.sample

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.util.Holders
import groovy.util.logging.Slf4j
import kafka.grails.GrailsSink
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@Slf4j
@EnableBinding([Source, GrailsSink])
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Bean
    MessageProducer messageProducer() {
        new MessageProducer()
    }

    @Bean
    @Profile("development")
    TraceConsumer traceConsumer() {
        new TraceConsumer()
    }

    @Override
    void doWithApplicationContext() {
        def messageProducer = Holders.applicationContext.getBean(MessageProducer)
        startMessageProducerPeriodically(messageProducer)
    }

    private static void startMessageProducerPeriodically(MessageProducer messageProducer) {
        Thread.start {
            try {
                while (true) {
                    sleep 5000
                    messageProducer.produce("Hello")
                }
            } catch (e) {
                log.error "Failed and terminated running MessageProducer periodically", e
            }
        }
    }
}
