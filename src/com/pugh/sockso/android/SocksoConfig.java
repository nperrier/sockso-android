package com.pugh.sockso.android;

// represents a sockso configuration
public class SocksoConfig {

	public static final int DEFAULT_PORT = 4444;
	
	private String server;
	private String user;
	private int    port = DEFAULT_PORT;	
	private String password;

	public SocksoConfig(){}
	
	public SocksoConfig(String server){
		this(null, null, server, DEFAULT_PORT);
	}
	
	public SocksoConfig(String server, int port){
		this(null, null, server, port);
	}
	
	public SocksoConfig(String user, String password, String server){
		this(user, password, server, DEFAULT_PORT);
	}
	
	public SocksoConfig(String user, String password, String server, int port){
		this.server   = server;
		this.port     = port;
		this.user     = user;
		this.password = password;
	}
	
	public void setServer(String server){
		this.server = server;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public void setUser(String user){
		this.user = user;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getServer(){
		return server;
	}
	
	public int getPort(){
		return port;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getPassword(){
		return password;
	}

}
