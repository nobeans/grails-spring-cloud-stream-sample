package sample

class BootStrap {

    MessageProducer messageProducer

    def init = { servletContext ->
        messageProducer.startProducingPeriodicallyInThread("Hello", 5000)
    }
}
