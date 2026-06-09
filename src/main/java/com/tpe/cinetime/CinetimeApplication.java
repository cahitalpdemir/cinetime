package com.tpe.cinetime;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CinetimeApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.directory(System.getProperty("user.dir"))
				.ignoreIfMissing()
				.load();

		Map<String, Object> envProperties = new HashMap<>();
		dotenv.entries().forEach(entry -> {
			envProperties.put(entry.getKey(), entry.getValue());
			System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication app = new SpringApplication(CinetimeApplication.class);
		app.setDefaultProperties(envProperties);
		app.run(args);
	}

}
