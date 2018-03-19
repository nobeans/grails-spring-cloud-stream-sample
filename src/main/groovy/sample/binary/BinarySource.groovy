package sample.binary

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

interface BinarySource {

    String OUTPUT = "binary-output"

    @Output(BinarySource.OUTPUT)
    MessageChannel output()
}
