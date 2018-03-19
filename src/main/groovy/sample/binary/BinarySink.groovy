package sample.binary

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

interface BinarySink {

    String INPUT = "binary-input"

    @Input(BinarySink.INPUT)
    SubscribableChannel input()
}
