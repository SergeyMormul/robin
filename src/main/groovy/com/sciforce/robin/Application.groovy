package com.sciforce.robin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@SpringBootApplication
@ServletComponentScan
class Application {

	static void main(String[] args) {
		SpringApplication.run Application, args
	}
}
