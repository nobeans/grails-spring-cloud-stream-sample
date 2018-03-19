package sample.binary

import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.RandomStringUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
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
    BinarySink binarySink

    @Rule
    TemporaryFolder tempFolder = new TemporaryFolder()

    byte[] binaryData = RandomStringUtils.random(1500).bytes

    AtomicReference<Throwable> failed = new AtomicReference<>()

    AtomicReference<List<Byte>> receivedBytes = new AtomicReference<>([])

    MessageHandler handler = {
        AtomicInteger sequenceId = new AtomicInteger(1)
        return { Message<String> message ->
            try {
                def json = new JsonSlurper().parseText(message.payload)
                assert json['key'] == "TEST_KEY"
                assert json['status'] == (json['data'] ? ChunkStatus.KEEP_ALIVE : ChunkStatus.END_OF_DATA).name()
                assert json['sequenceId'] == sequenceId.getAndIncrement()
                receivedBytes.get().addAll json['data'].decodeBase64() as List<Byte>
            } catch (Throwable e) {
                failed.set(e)
            }
        }
    }.call()

    void setup() {
        binarySink.input().subscribe(handler)
    }

    void cleanup() {
        binarySink.input().unsubscribe(handler)
    }

    void "produce from data array in memory"() {
        given:
        def ins = new ByteArrayInputStream(binaryData)

        when:
        binaryProducer.produce("TEST_KEY", ins)

        and:
        sleep 1000

        then:
        !failed.get()

        and:
        receivedBytes.get() as byte[] == binaryData
    }

    void "produce from large file"() {
        given:
        def dataFile = tempFolder.newFile("test.dat")
        startWriteData(dataFile, 5000)

        when:
        dataFile.withInputStream { InputStream ins ->
            binaryProducer.produce("TEST_KEY", ins)
        }

        and:
        sleep 1000

        then:
        !failed.get()

        and:
        receivedBytes.get().size() > 0
    }

    private void startWriteData(File file, int durationMsec) {
        Thread.start {
            long startTime = new Date().time
            file.withOutputStream { OutputStream out ->
                while (new Date().time - startTime < durationMsec) {
                    new ByteArrayInputStream(binaryData).withCloseable { InputStream ins ->
                        IOUtils.copyLarge(ins, out, 0, 256)
                        sleep 100
                    }
                }
            }
        }
    }
}
