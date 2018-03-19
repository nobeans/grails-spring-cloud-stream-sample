package sample

class BootStrap {

    TextProducer textProducer
    TraceConsumer traceConsumer

    def init = { servletContext ->
        environments {
            development {
                textProducer.startProducingPeriodicallyInThread("Hello", 5000)
                traceConsumer.subscribe()
            }
        }
    }
}
