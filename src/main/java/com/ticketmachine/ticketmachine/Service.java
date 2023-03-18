package com.ticketmachine.ticketmachine;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {
    private long id;
    private String name;
    @JsonProperty("office_name")
    private String officeName;
    @JsonProperty("waiting_time")
    private int waitingTime; // In minutes
    @JsonProperty("active_queues")
    private int activeQueues;
    
    
    
	public Service() {
		super();
	}
	
	public Service(long id, String name, String officeName, int waitingTime, int activeQueues) {
		super();
		this.id = id;
		this.name = name;
		this.officeName = officeName;
		this.waitingTime = waitingTime;
		this.activeQueues = activeQueues;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public int getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	public int getActiveQueues() {
		return activeQueues;
	}
	public void setActiveQueues(int activeQueues) {
		this.activeQueues = activeQueues;
	}
    
    
}
