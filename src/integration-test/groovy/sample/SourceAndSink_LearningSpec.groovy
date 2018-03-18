package sample

import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import grails.GrailsSink
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

@Ignore("This works only without spring-cloud-stream-test-support")
@Integration
class SourceAndSink_LearningSpec extends Specification {

    @Autowired
    Source source

    @Autowired
    GrailsSink grailsSink

    String testMessage = "TEST_MESSAGE-${new Date().format(/yyyyMMdd-HHmmss-SSS/)}"

    AtomicBoolean succeeded = new AtomicBoolean(false)

    MessageHandler handler = { Message<String> message ->
        def actualText = new JsonSlurper().parseText(message.payload).text
        succeeded.set(actualText == testMessage)
        println ">" * 50
        println "Actual:   $actualText"
        println "Expected: $testMessage"
        println "<" * 50
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
        succeeded.get()

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
        succeeded.get()

        cleanup:
        grailsSink.input().unsubscribe(handler)
    }
}
