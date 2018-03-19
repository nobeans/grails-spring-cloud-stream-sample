package sample

import sample.binary.BinaryConsumer
import sample.binary.BinaryProducer
import sample.text.TextConsumer
import sample.text.TextProducer

class BootStrap {

    TextProducer textProducer
    TextConsumer textConsumer
    BinaryProducer binaryProducer
    BinaryConsumer binaryConsumer

    def init = { servletContext ->
        environments {
            development {
                textProducer.startProducingPeriodicallyInThread("Hello", 5000)
                textConsumer.subscribe()
                binaryProducer.watchDirectory()
                binaryConsumer.subscribe()
            }
        }
    }
}
