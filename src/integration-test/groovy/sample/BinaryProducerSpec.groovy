package sample

import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.Message
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
@Integration
class BinaryProducerSpec extends Specification {

    @Autowired
    BinaryProducer binaryProducer

    @Autowired
    MessageCollector messageCollector

    byte[] binaryData = RandomStringUtils.random(2500).bytes

    void test() {
        given:
        def ins = new ByteArrayInputStream(binaryData)

        when:
        binaryProducer.produce("TEST_KEY", ins)

        then:
        def sequenceId = 1
        def dataBytes = []
        messageCollector.forChannel(binaryProducer.source.output()).<Message<String>> each { Message<String> message ->
            def json = new JsonSlurper().parseText(message.payload)
            assert json['key'] == "TEST_KEY"
            assert json['sequenceId'] == sequenceId++
            dataBytes.addAll json['data'].decodeBase64()
        }
        dataBytes as byte[] == binaryData
    }
}
