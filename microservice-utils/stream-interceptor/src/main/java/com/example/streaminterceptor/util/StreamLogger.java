package com.example.streaminterceptor.util;

import com.example.streaminterceptor.model.StreamPacket;

public interface StreamLogger {
    void send(StreamPacket streamPacket);
}
