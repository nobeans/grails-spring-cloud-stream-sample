package sample

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Bean
import sample.binary.BinaryConsumer
import sample.binary.BinaryProducer
import sample.binary.BinarySink
import sample.binary.BinarySource
import sample.text.TextProducer
import sample.text.TextSink
import sample.text.TextSource
import sample.text.TextConsumer

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
    TextConsumer textConsumer() {
        new TextConsumer()
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
