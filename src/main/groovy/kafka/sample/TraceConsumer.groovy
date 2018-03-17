package kafka.sample

import groovy.util.logging.Slf4j
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.stereotype.Component

@Slf4j
@Component
@EnableBinding(Sink)
class TraceConsumer {

    @StreamListener(Sink.INPUT)
    void trace(String payload) {
        log.info "Consumed: " + payload
    }
}
