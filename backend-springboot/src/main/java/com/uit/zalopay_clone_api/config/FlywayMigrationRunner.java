package com.uit.zalopay_clone_api.config;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FlywayMigrationRunner {

    private final DataSource dataSource;

    public FlywayMigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
        runFlywayMigration();
    }

    private void runFlywayMigration() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .outOfOrder(false)
                    .load();

            flyway.migrate();
            System.out.println("✓ Flyway migration completed successfully!");
        } catch (Exception e) {
            System.err.println("✗ Flyway migration failed: " + e.getMessage());
            throw new RuntimeException("Flyway migration failed", e);
        }
    }
}

