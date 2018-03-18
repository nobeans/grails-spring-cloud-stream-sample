package kafka.sample

import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Source
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

@Integration
class Source_withTestSupport_LearningSpec extends Specification {

    @Autowired
    Source source

    @Autowired
    MessageCollector messageCollector

    String testMessage = "TEST_MESSAGE-${new Date().format(/yyyyMMdd-HHmmss-SSS/)}"

    void "send"() {
        when:
        source.output().send(MessageBuilder.withPayload([text: testMessage]).build())

        then:
        String receivedMessage = messageCollector.forChannel(source.output()).poll().payload
        def actualText = new JsonSlurper().parseText(receivedMessage).text
        actualText == testMessage
    }
}
