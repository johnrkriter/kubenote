package com.example.restinterceptor.util;

import com.example.restinterceptor.filter.Packet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoPublicLog implements PublicLog {

    public NoPublicLog() {

    }

    @Override
    public void send(Packet packet) {

    }

    @Override
    public void sendExternal(Packet packet) {

    }

    @Override
    public void sendInternal(Packet packet) {

    }
}
