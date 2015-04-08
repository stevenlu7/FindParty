package com.partysearch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.firebase.client.ServerValue;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;


public class Room {

	private String userName;
	private int level;
	private String note;
	private String gametype;
	private Long time;
	
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
    
    public String formatTime(long time){
    	SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm" );
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(time);
    	cal.add(Calendar.HOUR, -3); //edt to pst
    	String timestamp = dateFormat.format(cal.getTime());
    	return timestamp;
    }
}
