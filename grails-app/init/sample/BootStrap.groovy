package sample

class BootStrap {

    TextProducer textProducer

    def init = { servletContext ->
        textProducer.startProducingPeriodicallyInThread("Hello", 5000)
    }
}
