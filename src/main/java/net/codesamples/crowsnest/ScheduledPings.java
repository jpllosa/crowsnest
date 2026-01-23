package net.codesamples.crowsnest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledPings {
    private static final Logger log = LoggerFactory.getLogger(ScheduledPings.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    EnvironmentConfigurationWatcher environmentConfigurationWatcher;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    private WebClient webClient = WebClient.create();

    private List<Environment> internalEnvironmentList = new ArrayList<>();

    private ObjectMapper mapper = new ObjectMapper();

    @Scheduled(cron = "${cron.expression}")
    public void pinger() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        List<Environment> environmentList = environmentConfigurationWatcher.getEnvironmentList();
        log.info("Environment list: {}", environmentList);

        for (Environment environment : environmentList) {
            for (App app : environment.getApps()) {
                Mono<String> mono = webClient.get()
                        .uri(app.getUrl())
                        .exchangeToMono(clientResponse -> {
                            HttpStatusCode statusCode = clientResponse.statusCode();
                            if (statusCode.is2xxSuccessful()) {
                                app.setStatus("up");
                            }
                            log.info("StatusCode: {} = {}", statusCode, app.getUrl());
                            return clientResponse.bodyToMono(String.class);
                        })
                        .onErrorResume(Exception.class, exception -> {
                            log.info("Exception {}", exception.getMessage());
                            app.setStatus("down");
                            return Mono.empty();
                        });
                mono.subscribe();
            }
        }

        if (internalEnvironmentList.isEmpty()) {
            internalEnvironmentList = deepCopy(environmentList);
            simpMessagingTemplate.convertAndSend("/topic/environments", internalEnvironmentList);
        } else {
            if (!areTheSame(environmentList, internalEnvironmentList)) {
                log.info("environmentList NOT equal to internalEnvironmentList: \n {} \n {}",
                        environmentList, internalEnvironmentList);
                internalEnvironmentList = deepCopy(environmentList);
                simpMessagingTemplate.convertAndSend("/topic/environments", internalEnvironmentList);
            } else {
                log.info("environmentList equal to internalEnvironmentList");
            }
        }
    }

    private List<Environment> deepCopy(List<Environment> src) {
        log.info("Deep copying environmentList to internalEnvironmentList");
        try {
            Environment[] environments = mapper.readValue(
                    mapper.writeValueAsString(src.toArray()), Environment[].class);
            return Arrays.asList(environments);
        } catch (JsonProcessingException e) {
            log.error("Unable to deep copy environment list, {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    private boolean areTheSame(List<Environment> list1, List<Environment> list2) {
        try {
            String s1 = mapper.writeValueAsString(list1.toArray());
            String s2 = mapper.writeValueAsString(list2.toArray());

            return s1.equalsIgnoreCase(s2);
        } catch (JsonProcessingException e) {
            log.error("Unable to compare environment list, {}", e.getMessage());
        }

        return false;
    }
}
