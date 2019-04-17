package com.example.cloudconfighelper;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import com.example.cloudconfighelper.model.ContextRefreshStore;

import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ContextRefreshMonitor {

    /**
     * Store refreshed keys to ContextRefreshStore object, which will be logged later.
     * @param keys List of refreshed configuration keys
     */
    @AfterReturning(pointcut = "execution(* org.springframework.cloud.context.refresh.ContextRefresher.refresh(..))", returning = "keys")
    public void contextRefreshMonitor(Set<String> keys){
        ContextRefreshStore.storeRefreshedKeys(keys);
        log.debug("Refreshed configuration keys : {}", keys);
    }

}

