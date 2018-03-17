package kafka.sample

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Slf4j
@Component
@EnableBinding(Source)
class MessageProducer {

    @Autowired
    Source source

    void produce(String text) {
        def message = MessageBuilder.withPayload([text: text]).build()
        source.output().send(message)
        log.info "Produced: ${message.dump()}"
    }
}
