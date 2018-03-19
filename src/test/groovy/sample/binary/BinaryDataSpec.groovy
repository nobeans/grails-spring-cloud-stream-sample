package sample.binary

import spock.lang.Unroll
import test.ConstraintUnitSpec

class BinaryDataSpec extends ConstraintUnitSpec {

    BinaryData binaryData = new BinaryData()

    @Unroll
    def "validate: #field is #error when value is #value.inspect()"() {
        given:
        bind(binaryData, field, value)

        expect:
        validateConstraints(binaryData, field, error)

        where:
        field        | error      | value
        "key"        | "nullable" | null
        "key"        | "blank"    | ""
        "key"        | "valid"    | "VALID_KEY"
        "status"     | "nullable" | null
        "status"     | "nullable" | ""
        "status"     | "valid"    | ChunkStatus.KEEP_ALIVE.name()
        "status"     | "valid"    | ChunkStatus.END_OF_DATA.name()
        "sequenceId" | "nullable" | null
        "sequenceId" | "nullable" | ""
        "sequenceId" | "valid"    | "123"
    }

    @Unroll
    def "validate: data is #error when value is #value.inspect() and status is #status.inspect()"() {
        given:
        binaryData.status = status

        and:
        bind(binaryData, 'data', value)

        expect:
        validateConstraints(binaryData, 'data', error)

        where:
        status                  | error               | value
        ChunkStatus.KEEP_ALIVE  | "validator.invalid" | null
        ChunkStatus.KEEP_ALIVE  | "validator.invalid" | ""
        ChunkStatus.KEEP_ALIVE  | "valid"             | "TEST_DATA".encodeAsBase64()
        ChunkStatus.END_OF_DATA | "valid"             | null
        ChunkStatus.END_OF_DATA | "valid"             | ""
        ChunkStatus.END_OF_DATA | "valid"             | "TEST_DATA".encodeAsBase64()
    }

    def "pseudo property rawData"() {
        given:
        def rawData = "TEST_DATA".bytes

        when:
        binaryData.rawData = rawData

        then:
        binaryData.rawData == rawData

        and:
        !binaryData.data.empty
    }
}

