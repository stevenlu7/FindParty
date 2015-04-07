package com.partysearch;


public class Room {

	private String userName;
	private int level;
	private String note;
	private String gametype;
	
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Room() {
    }
	
	public Room(String userName, int level, String note, String gametype){
		this.userName = userName;
		this.level = level;
		this.note = note;
		this.gametype = gametype;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public int getLevel(){
		return level;
	}
	
	public String getNote(){
		return note;
	}
	
	public String getGametype(){
		return gametype;
	}
}
