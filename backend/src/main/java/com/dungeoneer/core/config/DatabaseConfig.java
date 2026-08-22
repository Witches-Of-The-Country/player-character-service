package com.dungeoneer.core.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.dungeoneer.playerCharacter.adapter.out.persistence")
@EntityScan(basePackages = "com.dungeoneer.playerCharacter.adapter.out.persistence")
public class DatabaseConfig {
}
