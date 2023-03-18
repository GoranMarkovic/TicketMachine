package com.ticketmachine.ticketmachine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MobileUser {
    private long id;
    @JsonIgnore
    @JsonProperty("device_id")
    private String deviceId;
    
    
	public MobileUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MobileUser(long id, String deviceId) {
		super();
		this.id = id;
		this.deviceId = deviceId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
    
    
}

