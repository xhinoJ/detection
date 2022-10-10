package org.thfabric.threatmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ThreatManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreatManagementApplication.class, args);
    }

}
