package com.codeva.admin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = CodevaProperties.CODEVA_PREFIX)
public class CodevaProperties {

    public static final String CODEVA_PREFIX = "codeva";

    private Cron cron;
    private Weixin weixin;

    public CodevaProperties() {
        this.cron = new Cron();
        this.weixin = new Weixin();
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

    public static class Weixin {
        private String appId;
        private String appSecret;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    public Cron getCron() {
        return cron;
    }

    public void setCron(Cron cron) {
        this.cron = cron;
    }

    public Weixin getWeixin() {
        return weixin;
    }

    public void setWeixin(Weixin weixin) {
        this.weixin = weixin;
    }
}
