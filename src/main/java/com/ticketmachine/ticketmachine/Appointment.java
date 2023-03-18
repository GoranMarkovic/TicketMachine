package com.ticketmachine.ticketmachine;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Appointment {
    private long id;
    @JsonProperty("mobile_user")
    private MobileUser mobileUser;
    private Service service;
    private String tag;
    @JsonProperty("created_time")
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime createdTime;
    @JsonProperty("called_out_time")
    private LocalDateTime calledOutTime;
    @JsonProperty("finished_time")
    private LocalDateTime finishedTime;
    @JsonProperty("in_progress")
    private boolean inProgress;
    private boolean finished;
    
    
    
	public Appointment() {
		super();
	}
	public Appointment(long id, MobileUser mobileUser, Service service, String tag, LocalDateTime createdTime,
			LocalDateTime calledOutTime, LocalDateTime finishedTime, boolean inProgress, boolean finished) {
		super();
		this.id = id;
		this.mobileUser = mobileUser;
		this.service = service;
		this.tag = tag;
		this.createdTime = createdTime;
		this.calledOutTime = calledOutTime;
		this.finishedTime = finishedTime;
		this.inProgress = inProgress;
		this.finished = finished;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public MobileUser getMobileUser() {
		return mobileUser;
	}
	public void setMobileUser(MobileUser mobileUser) {
		this.mobileUser = mobileUser;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public LocalDateTime getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}
	public LocalDateTime getCalledOutTime() {
		return calledOutTime;
	}
	public void setCalledOutTime(LocalDateTime calledOutTime) {
		this.calledOutTime = calledOutTime;
	}
	public LocalDateTime getFinishedTime() {
		return finishedTime;
	}
	public void setFinishedTime(LocalDateTime finishedTime) {
		this.finishedTime = finishedTime;
	}
	public boolean isInProgress() {
		return inProgress;
	}
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
    
    
    
}
