package com.ft;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AsanaBot {

    @Autowired
    private Config config;


    @Bean(name = "graphicsAsanaClient")
    public AsanaClient getGraphicsAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_GRAPHICS_KEY"), config);
    }

    @Bean(name = "picturesAsanaClient")
    public AsanaClient getPicturesAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_PICTURES_KEY"), config);
    }

    @Bean(name = "reportAsanaClient")
    public AsanaClient getReportAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_REPORT_KEY"), config);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsanaBot.class, args);
    }
}