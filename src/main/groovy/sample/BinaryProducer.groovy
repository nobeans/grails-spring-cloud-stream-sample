package sample

import groovy.util.logging.Slf4j
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder

@Slf4j
class BinaryProducer {

    private static final int DEFAULT_CHUNK_SIZE = 1024 // bytes

    int chunkSize = DEFAULT_CHUNK_SIZE // bytes

    //@Autowired
    Source source

    void produce(String key, InputStream ins) throws IOException {
        eachChunk(chunkSize, ins) { int sequenceId, byte[] data ->
            sendData(key, sequenceId, data)
        }
        log.info "Produced data: key=$key"
    }

    private void sendData(String key, int sequenceId, byte[] data) {
        String encoded = data.encodeBase64()
        def message = MessageBuilder.withPayload([key: key, sequenceId: sequenceId, data: encoded]).build()
        source.output().send(message)
        log.info "Produced chunk: key=$key, sequenceId=$sequenceId, chunk=$encoded"
    }

    private static void eachChunk(int chunkSize, InputStream ins, Closure<Void> closure) throws IOException {
        byte[] buffer = new byte[chunkSize]
        int readLength = -1
        int sequenceId = 1
        while ((readLength = ins.read(buffer, 0, buffer.length)) != -1) {
            byte[] data = new byte[readLength]
            System.arraycopy(buffer, 0, data, 0, readLength)
            closure.call(sequenceId++, data)
        }
    }
}
