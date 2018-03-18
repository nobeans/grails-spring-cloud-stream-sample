package kafka.sample

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder

@Slf4j
class MessageProducer {

    @Autowired
    Source source

    void produce(String text) {
        def message = MessageBuilder.withPayload([text: text]).build()
        source.output().send(message)
        log.info "Produced: ${message.dump()}"
    }
}
