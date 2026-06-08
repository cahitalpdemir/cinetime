package com.tpe.cinetime;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CinetimeApplication {

	public static void main(String[] args) {

		// .env dosyasını okuma
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // .env dosyası yoksa hata vermez
				.load();

		// .env dosyasındaki değerleri sistem özelliklerine ekleme
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(CinetimeApplication.class, args);
	}

}
