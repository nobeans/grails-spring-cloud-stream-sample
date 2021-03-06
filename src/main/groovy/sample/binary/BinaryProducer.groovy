package sample.binary

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.support.MessageBuilder
import sample.io.FileWatcher

@Slf4j
class BinaryProducer {

    private static final int EOF = -1

    @Autowired
    BinarySource binarySource

    @Value('${sample.binaryProducer.chunkSize}')
    int chunkSize

    @Value('${sample.binaryProducer.retryInterval}')
    long retryInterval

    @Value('${sample.binaryProducer.retryMaxCount}')
    int retryMaxCount

    @Value('${sample.binaryProducer.watchDir}')
    File watchDir

    void watchDirectory() {
        watchDir.mkdirs()
        new FileWatcher(targetDir: watchDir).watch { File file ->
            file.withInputStream { InputStream ins ->
                produce(file.name, ins)
            }
        }
    }

    void produce(String key, InputStream ins) throws IOException {
        long sequenceId = 1
        eachChunk(ins, chunkSize, retryMaxCount) { byte[] data ->
            sendData(key, ChunkStatus.KEEP_ALIVE, sequenceId++, data)
        }
        sendData(key, ChunkStatus.END_OF_DATA, sequenceId, [] as byte[])
        log.info "Produced data: key=$key"
    }

    private void sendData(String key, ChunkStatus status, long sequenceId, byte[] data) {
        def binaryData = new BinaryData(key: key, status: status, sequenceId: sequenceId, rawData: data)
        if (!binaryData.validate()) {
            throw new RuntimeException("Invalid data: binaryData=${binaryData.dump()}")
        }
        def message = MessageBuilder.withPayload(binaryData.toMap()).build()
        binarySource.output().send(message)
        log.info "Produced chunk: key=$key, status=$status, sequenceId=$sequenceId"
    }

    private void eachChunk(InputStream ins, int chunkSize, int remainRetryCount, Closure<Void> closure) throws IOException {
        byte[] buffer = new byte[chunkSize]
        int readLength = -1
        while ((readLength = ins.read(buffer, 0, buffer.length)) != EOF) {
            // Reset count when data is found
            remainRetryCount = retryMaxCount

            byte[] data = new byte[readLength]
            System.arraycopy(buffer, 0, data, 0, readLength)
            closure.call(data)
        }
        log.info "EOF found"

        // Retrying if possible
        if (remainRetryCount <= 0) {
            log.info "Retry count exceeded"
            return
        }
        log.info "Sleeping and retrying...: retryInterval=$retryInterval, remainRetryCount=${remainRetryCount - 1}"
        sleep retryInterval
        eachChunk(ins, chunkSize, remainRetryCount - 1, closure)
    }
}
