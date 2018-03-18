package kafka.sample

import grails.util.Holders

class BootStrap {

    def init = { servletContext ->
        def messageProducer = Holders.applicationContext.getBean(MessageProducer)
        startMessageProducerPeriodically(messageProducer)
    }

    private static void startMessageProducerPeriodically(MessageProducer messageProducer) {
        Thread.start {
            try {
                while (true) {
                    sleep 5000
                    messageProducer.produce("Hello")
                }
            } catch (e) {
                log.error "Failed and terminated running MessageProducer periodically", e
            }
        }
    }
}
