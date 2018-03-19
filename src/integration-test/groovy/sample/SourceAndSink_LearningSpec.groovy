package sample

import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference

@Integration
class SourceAndSink_LearningSpec extends Specification {

    @Autowired
    Source source

    @Autowired
    GrailsSink grailsSink

    String testMessage = "TEST_MESSAGE-${new Date().format(/yyyyMMdd-HHmmss-SSS/)}"

    AtomicReference<Throwable> failed = new AtomicReference<>()

    MessageHandler handler = { Message<String> message ->
        try {
            def actualText = new JsonSlurper().parseText(message.payload).text
            assert actualText == testMessage
            println ">" * 50
            println "Actual:   $actualText"
            println "Expected: $testMessage"
            println "<" * 50
        } catch (Throwable e) {
            failed.set(e)
        }
    }

    void "subscribe and then send"() {
        when:
        grailsSink.input().subscribe(handler)

        and:
        sleep 500

        and:
        source.output().send(MessageBuilder.withPayload([text: testMessage]).build())

        and:
        sleep 1000

        then:
        !failed.get()

        cleanup:
        grailsSink.input().unsubscribe(handler)
    }

    void "send and then subscribe"() {
        when:
        source.output().send(MessageBuilder.withPayload([text: testMessage]).build())

        and:
        grailsSink.input().subscribe(handler)

        and:
        sleep 1000

        then:
        !failed.get()

        cleanup:
        grailsSink.input().unsubscribe(handler)
    }
}
