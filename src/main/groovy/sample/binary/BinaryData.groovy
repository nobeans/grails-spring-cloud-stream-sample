package sample.binary

import grails.validation.Validateable
import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true, excludes = ['data'])
class BinaryData implements Validateable {

    String key
    ChunkStatus status
    Long sequenceId
    String data

    static constraints = {
        key blank: false
        status()
        sequenceId min: 1L
        data nullable: true, validator: { String value, BinaryData self ->
            if (self.status == ChunkStatus.KEEP_ALIVE) {
                return value != null && !value.empty
            }
            return true
        }
    }

    byte[] getRawData() {
        (data?.decodeBase64() ?: []) as byte[]
    }

    void setRawData(byte[] rawData) {
        data = rawData.encodeAsBase64()
    }

    Map<String, ?> toMap() {
        [key: key, status: status, sequenceId: sequenceId, data: data]
    }
}
