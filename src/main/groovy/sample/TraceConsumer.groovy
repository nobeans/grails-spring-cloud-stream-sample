package sample

import groovy.util.logging.Slf4j
import org.springframework.cloud.stream.annotation.StreamListener

@Slf4j
class TraceConsumer {

    @StreamListener(GrailsSink.INPUT)
    void trace(Map<String, Object> payload) {
        log.info "Consumed: " + payload.dump()
    }
}
