package com.ticketmachine.ticketmachine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JSONParser {
	private static final ObjectMapper objectMapper = new ObjectMapper();


	public static Service createServiceObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, Service.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<Service> createServiceListObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, new TypeReference<List<Service>>(){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Appointment createAppointmentObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, Appointment.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static JWT createJWTObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, JWT.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static List<Appointment> createAppointmentListObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, new TypeReference<List<Appointment>>(){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<AppointmentInfoResponse> createAppointmentInfoResponseListObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, new TypeReference<List<AppointmentInfoResponse>>(){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static AppointmentInfoResponse createAppointmentInfoResponseObject(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, AppointmentInfoResponse.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
