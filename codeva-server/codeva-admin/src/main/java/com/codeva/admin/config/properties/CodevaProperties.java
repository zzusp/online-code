package com.codeva.admin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = CodevaProperties.CODEVA_PREFIX)
public class CodevaProperties {

    public static final String CODEVA_PREFIX = "codeva";

    private Cron cron;

    public CodevaProperties() {
        this.cron = new Cron();
    }

    public static class Cron {
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public Cron getCron() {
        return cron;
    }

    public void setCron(Cron cron) {
        this.cron = cron;
    }
}
