package com.ticketmachine.ticketmachine;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class AppointmentInfoResponse {
    private Appointment appointment;
    @JsonProperty("clients_in_front")
    private int clientsInFront;
    @JsonProperty("arrival_time")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime arrivalTime;
    
    
    
	public AppointmentInfoResponse() {
		super();
	}
	public AppointmentInfoResponse(Appointment appointment, int clientsInFront, LocalDateTime arrivalTime) {
		super();
		this.appointment = appointment;
		this.clientsInFront = clientsInFront;
		this.arrivalTime = arrivalTime;
	}
	public Appointment getAppointment() {
		return appointment;
	}
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	public int getClientsInFront() {
		return clientsInFront;
	}
	public void setClientsInFront(int clientsInFront) {
		this.clientsInFront = clientsInFront;
	}
	public LocalDateTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(LocalDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
}
