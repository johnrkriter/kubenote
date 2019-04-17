package com.example.streaminterceptor.util;

import com.example.streaminterceptor.model.StreamPacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamLogNoOpt implements StreamLogger {

    public StreamLogNoOpt() {
        log.info("--- Create bean StreamLogNoOpt ---");
    }

    @Override
    public void send(StreamPacket streamPacket) {
        // Logging is disabled. Do nothing.
    }

}
