package com.pugh.sockso.android;

// represents a sockso configuration
public class Config {

	public static final int DEFAULT_PORT = 4444;
	
	private String server;
	private String user;
	private int    port = DEFAULT_PORT;	
	private String password;

	public Config(){}
	
	public Config(String server){
		this(server, DEFAULT_PORT, null, null);
	}
	
	public Config(String server, int port){
		this(server, port, null, null);
	}
	
	public Config(String server, String user, String password){
		this(server, DEFAULT_PORT, user, password);
	}
	
	public Config(String server, int port, String user, String password){
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
