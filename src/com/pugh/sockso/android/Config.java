package com.pugh.sockso.android;

// represents a sockso configuration
public class Config {

    private String hostname;
    private int    port;
    private String user;
    private String password;

    public Config(String server, int port) {
        this(null, null, server, port);
    }

    public Config(String user, String password, String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
