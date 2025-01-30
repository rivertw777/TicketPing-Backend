package com.ticketPing.performance.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class RedissonLuaScriptConfig {

    private final RedissonReactiveClient redissonClient;

    @Bean
    public String PreReserveScript() throws IOException {
        return loadScript("scripts/preReserveScript.lua");
    }

    private String loadScript(String scriptPath) throws IOException {
        String script = StreamUtils.copyToString(
                new ClassPathResource(scriptPath).getInputStream(),
                StandardCharsets.UTF_8
        );
        return redissonClient.getScript().scriptLoad(script).block();
    }

}
