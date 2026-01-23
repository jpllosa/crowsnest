package net.codesamples.crowsnest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class EnvironmentsController {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentsController.class);

    @Autowired
    EnvironmentConfigurationWatcher environmentConfigurationWatcher;

    @MessageMapping("/environment")
    @SendTo("/topic/environments")
    public List<Environment> environments() {
        log.info("Message received and publishing...");
        return environmentConfigurationWatcher.getEnvironmentList();
    }
}

