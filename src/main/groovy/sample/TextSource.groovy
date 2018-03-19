package sample

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

interface TextSource {

    String OUTPUT = "text-output"

    @Output(TextSource.OUTPUT)
    MessageChannel output()
}
