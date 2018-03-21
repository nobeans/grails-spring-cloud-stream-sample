package sample.io

import groovy.util.logging.Slf4j

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

@Slf4j
class FileWatcher {

    File targetDir

    WatchService watcher

    void watch(Closure closure) {
        log.info "Start watching: targetDir=$targetDir"
        WatchService watcher = FileSystems.default.newWatchService()
        targetDir.toPath().register(watcher, StandardWatchEventKinds.ENTRY_CREATE)
        Thread.startDaemon {
            while (true) {
                WatchKey watchKey = watcher.take()
                log.info "watchKey: watchKey=${watchKey.dump()}"

                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    log.info "watchEvent: watchEvent=${watchEvent.dump()}"

                    if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
                        // TODO should handle correctly
                        log.warn "Too many events to handle correctly: watchEvent=${watchEvent.dump()}"
                    }

                    Path name = watchEvent.context() as Path
                    File file = targetDir.toPath().resolve(name).toFile()
                    log.info "Detected creation file: fie=${file}"

                    closure.call(file)
                }

                if (!watchKey.reset()) {
                    log.error "WatchKey is disabled: watchKey=$watchKey"
                }
            }
        }
    }

    void close() {
        watcher?.close()
        watcher = null
        log.info "Closed"
    }
}
