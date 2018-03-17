package kafka.sample

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.ComponentScan

@Slf4j
@ComponentScan("kafka.sample")
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
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
