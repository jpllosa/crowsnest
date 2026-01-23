package net.codesamples.crowsnest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EnvironmentConfigurationWatcher {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentConfigurationWatcher.class);

    List<Environment> environmentList = new ArrayList<>();

    private final String environmentsConfigFile = "environments.json";

    @Value("${watch.directory}")
    private String watchDirectory;

    @EventListener(ApplicationReadyEvent.class)
    public void startWatcher() {
        readConfig();

        new Thread(() -> {
            final Path path = FileSystems.getDefault().getPath(watchDirectory);
            try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
//            final WatchKey watchKey =
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    final WatchKey wk = watchService.take();
                    for (WatchEvent<?> event : wk.pollEvents()) {
                        //we only register "ENTRY_MODIFY" so the context is always a Path.
                        final Path changed = (Path) event.context();
                        if (changed.endsWith(environmentsConfigFile)) {
                            readConfig();
                        }
                    }
                    // reset the key
                    boolean valid = wk.reset();
                    if (!valid) {
                        log.info("Key has been unregistered");
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void readConfig() {
        ObjectMapper mapper = new ObjectMapper();
        File envConfigFile = new File(watchDirectory + "/" + environmentsConfigFile);

        try (InputStream is = new FileInputStream(envConfigFile); ) {
            Environment[] environments = mapper.readValue(is, Environment[].class);

            environmentList.clear();
            environmentList.addAll(Arrays.asList(environments));
            log.info("Envs updated, {}", environmentList);
        } catch (Exception e) {
            log.error("Unable to read envs config file, {}", e.getMessage());
        }
    }

    public List<Environment> getEnvironmentList() {
        return environmentList;
    }
}
