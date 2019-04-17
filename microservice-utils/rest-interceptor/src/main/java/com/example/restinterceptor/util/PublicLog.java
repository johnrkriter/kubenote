package com.example.restinterceptor.util;

import com.example.restinterceptor.filter.Packet;

public interface PublicLog {
    public void send(Packet packet);

    public void sendExternal(Packet packet);

    public void sendInternal(Packet packet);
}
