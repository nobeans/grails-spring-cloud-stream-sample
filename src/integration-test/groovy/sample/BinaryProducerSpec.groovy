package sample

import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@Integration
class BinaryProducerSpec extends Specification {

    @Autowired
    BinaryProducer binaryProducer

    @Autowired
    GrailsSink grailsSink

    byte[] binaryData = RandomStringUtils.random(1500).bytes

    AtomicReference<Throwable> failed = new AtomicReference<>()

    AtomicInteger sequenceId = new AtomicInteger(1)

    AtomicReference<List<Byte>> receivedBytes = new AtomicReference<>([])

    MessageHandler handler = { Message<String> message ->
        try {
            def json = new JsonSlurper().parseText(message.payload)
            assert json['key'] == "TEST_KEY"
            assert json['sequenceId'] == sequenceId.getAndIncrement()
            receivedBytes.get().addAll json['data'].decodeBase64() as List<Byte>
        } catch (Throwable e) {
            failed.set(e)
        }
    }

    void test() {
        given:
        grailsSink.input().subscribe(handler)

        and:
        sleep 1000

        and:
        def ins = new ByteArrayInputStream(binaryData)

        when:
        binaryProducer.produce("TEST_KEY", ins)

        and:
        sleep 1000

        then:
        !failed.get()

        and:
        receivedBytes.get() as byte[] == binaryData

        cleanup:
        grailsSink.input().unsubscribe(handler)
    }
}
