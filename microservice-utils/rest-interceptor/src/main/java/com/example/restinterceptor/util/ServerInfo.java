package com.example.restinterceptor.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerInfo {

    private static ServerInfo serverInfo;
    private static String hostName;
    private static String hostServer;

    private ServerInfo() {
    }

    private ServerInfo(String hostName, String hostServer) {
        this.hostName = hostName;
        this.hostServer = hostServer;
    }

    public static ServerInfo getInstance() {
        if (serverInfo == null) {
            return getHostDetail() ? new ServerInfo(hostName, hostServer) : new ServerInfo();
        } else {
            return serverInfo;
        }
    }

    private static boolean getHostDetail() {
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            hostServer = InetAddress.getLocalHost().getHostAddress();
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public String getHostName() {
        return hostName == null ? "" : hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostServer() {
        return hostServer == null ? "" : hostServer;
    }

    public void setHostServer(String hostServer) {
        this.hostServer = hostServer;
    }
}
