package com.pugh.sockso.android;

// represents a sockso configuration
public class Config {

    private String server;
    private int    port;
    private String user;
    private String password;

    public Config(String server, int port) {
        this(null, null, server, port);
    }

    public Config(String user, String password, String server, int port) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void setServer(String server) {
        this.server = server;
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

    public String getServer() {
        return server;
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
