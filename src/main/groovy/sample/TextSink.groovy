package sample

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

interface TextSink {

    String INPUT = "text-input"

    @Input(TextSink.INPUT)
    SubscribableChannel input()
}
