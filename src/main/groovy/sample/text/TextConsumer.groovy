package sample.text

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler

@Slf4j
class TextConsumer {

    @Autowired
    TextSink textSink

    // TODO The method with @StreamListener run when the bean is initialized. It's difficult to control start/stop.
    // TODO You can also use @Profile("development"). But in this sample, show you the programmatic way.
//    @StreamListener(TextSink.INPUT)
//    void consume(Map<String, Object> payload) {
//        log.info "Consumed: " + payload.dump()
//    }

    MessageHandler handler = { Message message ->
        log.info "Consumed: " + message.payload.dump()
    }

    void subscribe() {
        textSink.input().subscribe(handler)
    }

    void unsubscribe() {
        textSink.input().unsubscribe(handler)
    }
}
