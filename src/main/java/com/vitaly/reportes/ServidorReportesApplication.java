package com.vitaly.reportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class ServidorReportesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServidorReportesApplication.class, args);
	}

}
