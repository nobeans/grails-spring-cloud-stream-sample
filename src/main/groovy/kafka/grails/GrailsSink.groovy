package kafka.grails

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

/**
 * The Sink class for Grails.
 * <p>
 * The {@code INPUT} of {@link org.springframework.cloud.stream.messaging.Sink} conflicts to the Grail's bean name.
 */
interface GrailsSink {

    String INPUT = "grails-input"

    @Input(GrailsSink.INPUT)
    SubscribableChannel input()
}
