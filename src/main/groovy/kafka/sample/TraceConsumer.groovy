package kafka.sample

import groovy.util.logging.Slf4j
import kafka.grails.GrailsSink
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Component

@Slf4j
@Component
@EnableBinding(GrailsSink)
class TraceConsumer {

    @StreamListener(GrailsSink.INPUT)
    void trace(String payload) {
        log.info "Consumed: " + payload
    }
}
