package sample

import grails.testing.mixin.integration.Integration
import grails.GrailsSink
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.rule.OutputCapture
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

@Integration
class Sink_withTestSupport_LearningSpec extends Specification {

    @Autowired
    GrailsSink grailsSink

    @Rule
    OutputCapture capture = new OutputCapture()

    String testMessage = "TEST_MESSAGE-${new Date().format(/yyyyMMdd-HHmmss-SSS/)}"

    void "receive"() {
        when:
        // FIXME 何故かMapで渡すとJSONのデシリアライズエラーになってしまう
        //def message = MessageBuilder.withPayload([text: testMessage]).build()
        def message = MessageBuilder.withPayload('{"text":"' + testMessage + '"}').build()
        println message.dump()
        grailsSink.input().send(message)

        then:
        capture.toString().indexOf(testMessage) > -1
    }
}
