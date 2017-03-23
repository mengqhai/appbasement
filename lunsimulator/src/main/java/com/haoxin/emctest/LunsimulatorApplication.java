package com.haoxin.emctest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.haoxin.emctest.model.Lun;
import com.haoxin.emctest.repository.LunRepository;

@SpringBootApplication
public class LunsimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunsimulatorApplication.class, args);
	}
    
    @Bean
    public CommandLineRunner demoData(LunRepository lunRepository) {
    	return (args) -> {
    		if (lunRepository.count() < 2) {
    			lunRepository.save(new Lun(30000));
    			lunRepository.save(new Lun(60000));
    		}
    	};
    }
}
