package sample

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

    void startProducingPeriodicallyInThread(String message, int intervalMsec) {
        Thread.start {
            try {
                while (true) {
                    sleep intervalMsec
                    produce(message)
                }
            } catch (e) {
                log.error "Failed and terminated running MessageProducer periodically", e
            }
        }
    }
}
