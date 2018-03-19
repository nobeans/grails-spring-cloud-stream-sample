package sample.binary

import grails.databinding.SimpleDataBinder
import grails.databinding.SimpleMapDataBindingSource
import grails.validation.Validateable
import groovy.json.JsonSlurper
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.Message

@Slf4j
class BinaryConsumer {

    @Autowired
    BinarySink binarySink

    @Value('${sample.binaryConsumer.workDir}')
    File workDir

    @Value('${sample.binaryConsumer.storeDir}')
    File storeDir

    void subscribe() {
        binarySink.input().subscribe(this.&handler)
    }

    void unsubscribe() {
        binarySink.input().unsubscribe(this.&handler)
    }

    private void handler(Message<String> message) {
        def binaryData = parseBinaryData(message)
        if (binaryData.status == ChunkStatus.KEEP_ALIVE) {
            // TODO Expected that Kafka's data is ordered. (not using sequenceId so far)
            workDir.mkdirs()
            def workFile = new File(workDir, binaryData.key)
            workFile.append(binaryData.data)
            log.info "Appended data: workFile=$workFile, binaryData=$binaryData"
        } else if (binaryData.status == ChunkStatus.END_OF_DATA) {
            def workFile = new File(workDir, binaryData.key)
            if (!workFile.exists()) {
                log.warn "Work file not found: workFile=$workFile, binaryData=$binaryData"
            }
            storeDir.mkdirs()
            def storedFile = new File(storeDir, binaryData.key)
            workFile.renameTo(storedFile)
            log.info "Persisted file: storedFile=$storedFile, binaryData=$binaryData"
        } else {
            assert "Must not come here. If here, it's just a bug."
        }
    }

    private static BinaryData parseBinaryData(Message<String> message) {
        def json = new JsonSlurper().parseText(message.payload)
        def binaryData = new BinaryData()
        def map = new SimpleMapDataBindingSource(json as Map)
        new SimpleDataBinder().bind(binaryData, map)
        if (!binaryData.validate()) {
            throw new RuntimeException("Invalid data: json=$json, binaryData=${binaryData.dump()}")
        }
        return binaryData
    }

    @ToString(includeNames = true)
    static class BinaryData implements Validateable {
        String key
        ChunkStatus status
        Long sequenceId
        String dataEncoded

        static constraints = {
            key blank: false
            status()
            sequenceId blank: false
            dataEncoded blank: false
        }

        byte[] getData() {
            dataEncoded.decodeBase64() as byte[]
        }
    }
}
