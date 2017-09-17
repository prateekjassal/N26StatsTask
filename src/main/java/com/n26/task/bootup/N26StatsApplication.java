package com.n26.task.bootup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * Created by prateekjassal on 17/9/17.
 */
@EnableScheduling
@ComponentScan(basePackages = "com.n26.task")
@SpringBootApplication
public class N26StatsApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(N26StatsApplication.class, args);
	}
}
