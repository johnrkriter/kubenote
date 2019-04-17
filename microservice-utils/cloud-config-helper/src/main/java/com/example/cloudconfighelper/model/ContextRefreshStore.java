package com.example.cloudconfighelper.model;

import java.util.Set;

public class ContextRefreshStore {
    private static Set<String> refreshedKeys;

    public static void storeRefreshedKeys(Set<String> newKeys){
        refreshedKeys = newKeys;
    }

    public static Set<String> getRefreshKeys() {
        return refreshedKeys;
    }

    public static void clearRefreshedKeys(){
        refreshedKeys = null;
    }

    private ContextRefreshStore() {}
}
