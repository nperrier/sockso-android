package com.pugh.sockso.android.music;


public class Artist {

	private int id = 0;
	private String name;
	
	public Artist() {
	}

	public Artist(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return this.name;
	}	
}
