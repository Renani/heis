package com.renani.heis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeisKontrollSystemApp 
  implements CommandLineRunner {

    private static Logger LOG = LogManager
      .getLogger(HeisKontrollSystemApp.class);

    public static void main(String[] args) {
        LOG.info("Heis kontrollsystem starter");
        SpringApplication.run(HeisKontrollSystemApp.class, args);
        LOG.info("Heis kontrollsystem avslutter ");
    }

    
	public void run(String... args) throws Exception {
		
		
	}
 
  
}