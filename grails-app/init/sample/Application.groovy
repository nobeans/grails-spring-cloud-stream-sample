package sample

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean

@EnableBinding([Source, GrailsSink])
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Bean
    TextProducer textProducer() {
        new TextProducer()
    }

    @Bean
    TraceConsumer traceConsumer() {
        new TraceConsumer()
    }
}
