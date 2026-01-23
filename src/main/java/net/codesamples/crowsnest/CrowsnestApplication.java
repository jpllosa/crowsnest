package net.codesamples.crowsnest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrowsnestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrowsnestApplication.class, args);
	}
}
