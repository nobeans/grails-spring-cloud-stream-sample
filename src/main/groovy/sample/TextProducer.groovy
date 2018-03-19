package sample

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder

@Slf4j
class TextProducer {

    @Autowired
    TextSource textSource

    void produce(String text) {
        def message = MessageBuilder.withPayload([text: text]).build()
        textSource.output().send(message)
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
                log.error "Failed and terminated running TextProducer periodically", e
            }
        }
    }
}
