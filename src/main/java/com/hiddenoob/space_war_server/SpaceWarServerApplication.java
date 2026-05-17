package com.hiddenoob.space_war_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import com.hiddenoob.space_war_server.server.Game;

@SpringBootApplication
@EnableAsync
public class SpaceWarServerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext  context = SpringApplication.run(SpaceWarServerApplication.class, args);
		context.getBean(Game.class).start();
	}

}
