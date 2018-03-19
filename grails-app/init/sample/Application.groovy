package sample

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Bean

@EnableBinding([TextSource, TextSink, BinarySource, BinarySink])
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

    @Bean
    BinaryProducer binaryProducer() {
        new BinaryProducer()
    }

    @Bean
    BinaryConsumer binaryConsumer() {
        new BinaryConsumer()
    }
}
