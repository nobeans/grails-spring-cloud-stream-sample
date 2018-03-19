package sample.binary

import grails.databinding.SimpleDataBinder
import grails.databinding.SimpleMapDataBindingSource
import grails.validation.Validateable
import groovy.json.JsonSlurper
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message

@Slf4j
class BinaryConsumer {

    @Autowired
    BinarySink binarySink

    File storeDir = new File("/tmp/sample")

    void subscribe() {
        binarySink.input().subscribe(this.&handler)
    }

    void unsubscribe() {
        binarySink.input().unsubscribe(this.&handler)
    }

    private void handler(Message<String> message) {
        def binaryData = parseBinaryData(message)

        // TODO Expected that Kafka's data is ordered. (not using sequenceId so far)
        def file = new File(storeDir, binaryData.key)
        file.append(binaryData.data)

        log.info "Appended: file=$file, binaryData=$binaryData"
    }

    private static BinaryData parseBinaryData(Message<String> message) {
        def json = new JsonSlurper().parseText(message.payload)
        def binaryData = new BinaryData()
        def map = new SimpleMapDataBindingSource(json as Map)
        new SimpleDataBinder().bind(binaryData, map)
        if (!binaryData.validate()) {
            throw new RuntimeException("Invalid data: json=$json, binaryData=${binaryData.dump()}")
        }
    }

    @ToString(includeNames = true)
    static class BinaryData implements Validateable {
        String key
        Long sequenceId
        String dataEncoded

        static constraints = {
            key blank: false
            sequenceId blank: false
            dataEncoded blank: false
        }

        byte[] getData() {
            dataEncoded.decodeBase64() as byte[]
        }
    }
}
