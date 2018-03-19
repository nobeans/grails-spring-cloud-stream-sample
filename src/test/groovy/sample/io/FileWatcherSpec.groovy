package sample.io

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileWatcherSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder = new TemporaryFolder()

    FileWatcher fileWatcher

    void setup() {
        fileWatcher = new FileWatcher(targetDir: tempFolder.root)
    }

    void cleanup() {
        fileWatcher.close()
    }

    void "watch and close"() {
        given:
        def foundFiles = []

        when:
        fileWatcher.watch { File file ->
            println "Found $file"
            System.out.flush()
            foundFiles << file
        }

        and:
        3.times {
            tempFolder.newFile("test$it") << "TEST_$it"
            println "Create file: $it"
            sleep 5000
        }

        and:
        sleep 10_000

        then:
        foundFiles.collect { it.name } == ["test0", "test1", "test2"]
    }
}
