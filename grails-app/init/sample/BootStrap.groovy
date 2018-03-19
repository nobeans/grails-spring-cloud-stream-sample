package sample

class BootStrap {

    TextProducer textProducer

    def init = { servletContext ->
        environments {
            development {
                textProducer.startProducingPeriodicallyInThread("Hello", 5000)
            }
        }
    }
}
