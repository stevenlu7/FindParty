package com.partysearch;

import com.firebase.client.ServerValue;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Room {
	@JsonProperty("userName")
	private String username;
	@JsonProperty("level")
	private int level;
	@JsonProperty("note")
	private String note;
	@JsonProperty("gametype")
	private String gametype;
	@JsonProperty("time")
	private Long time;
	@JsonProperty("console")
	private String console;
	
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public Room() {
    }
	
	public Room(String username, int level, String note, String gametype, String console){
		this.username = username;
		this.level = level;
		this.note = note;
		this.gametype = gametype;
		this.console = console;
	}
	
	public String getUsername(){
		return username;
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
	
    public java.util.Map<String, String> getTime() {
        return ServerValue.TIMESTAMP;
    }
    
    @JsonIgnore
    public Long getTimeLong() {
       return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    
    public String getConsole(){
    	return console;
    }
    
    public String formatTime(long time){
    	SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm" );
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(time);
    	//cal.add(Calendar.HOUR, -3); //edt to pst
    	String timestamp = dateFormat.format(cal.getTime());
    	return timestamp;
    }
}
