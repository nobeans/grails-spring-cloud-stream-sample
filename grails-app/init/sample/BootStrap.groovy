package sample

import sample.binary.BinaryConsumer
import sample.text.TextProducer
import sample.text.TextConsumer

class BootStrap {

    TextProducer textProducer
    TextConsumer textConsumer
    BinaryConsumer binaryConsumer

    def init = { servletContext ->
        environments {
            development {
                textProducer.startProducingPeriodicallyInThread("Hello", 5000)
                textConsumer.subscribe()
                binaryConsumer.subscribe()
            }
        }
    }
}
