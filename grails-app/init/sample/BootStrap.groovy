package sample

class BootStrap {

    TextProducer textProducer
    TraceConsumer traceConsumer
    BinaryConsumer binaryConsumer

    def init = { servletContext ->
        environments {
            development {
                textProducer.startProducingPeriodicallyInThread("Hello", 5000)
                traceConsumer.subscribe()
                binaryConsumer.subscribe()
            }
        }
    }
}
