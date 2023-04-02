package com.ticketmachine.ticketmachine;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class SampleController 
{

	@FXML private BorderPane root;
	@FXML private Button button1;
	@FXML private Button button2;
	@FXML private Button button3;
	@FXML private Button button4;
	@FXML public Label labelDateAndTime;
	@FXML AnchorPane anchorPaneButtons;
	@FXML private GridPane buttonGridPane; 
	
	private static int counter=0;
	
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private List<Service> services;
	int globalValue;

	public SampleController()
	{
		this(0);
	}

	public SampleController(int globalValue) {
		this.globalValue=globalValue;
	}

	private Button[] buttons;
	private static String jwtToken="";
	@FXML
	public void initialize()
	{
		Timenow();
//		getToken(false);
//		postRequest();
		buttons= new Button[]{button1, button2, button3, button4};
		getAllServices(buttons);
	}
	
	private void getAllServices(Button[] buttons) {
		Thread thread = new Thread(){
	    	public void run() {
	    		try {
	    			services = getServices().get();
	    			
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		} catch (ExecutionException e) {
	    			e.printStackTrace();
	    		}
	    		
	            Platform.runLater(() -> {
	                 //pridruzi UI komponenti List<Service> services
	            	for(int i=0;i<4;i++)//4 zbog broja button
	            	{
	            		Service tmp=services.get(i);
	            		buttons[i].setText(tmp.getName());
	            		System.out.println(tmp.getName());
	            		System.out.println(tmp.getId());
	            	}
	            });
		        

	    	};
	    };
	    thread.start();
	}
	
	@FXML
	private void onClick(long serviceId) {
		//otvoriti dialog
		Stage stage=(Stage)root.getScene().getWindow();
		Dialog<Boolean> dialog=new Dialog<Boolean>();
		dialog.setTitle("Sačekajte listić");
		dialog.initOwner(stage);
		stage.setAlwaysOnTop(true);
		dialog.show();


		Thread thread = new Thread(){
	    	public void run() {
	    		try {
	    			AppointmentInfoResponse appointmentInfoResponse = createNewAppointment(serviceId).get();
					if(appointmentInfoResponse!=null)
					{
						Appointment appointment=appointmentInfoResponse.getAppointment();
						String clientsInFront=Integer.toString(appointmentInfoResponse.getClientsInFront());
						PrintClass pc=new PrintClass(appointment.getService().getName(), appointment.getService().getOfficeName(),clientsInFront,appointment.getTag(),
								appointment.getCreatedTime(),appointmentInfoResponse.getArrivalTime());
						pc.printNumber();

					}

				} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		} catch (ExecutionException e) {
	    			e.printStackTrace();
	    		}
	    		
	            Platform.runLater(() -> {
	                 dialog.setResult(true);
					 dialog.close();
	            });
		        

	    	};
	    };
	    thread.start();
	}

	private String getServicesRequest() throws IOException {
		StringBuffer content = new StringBuffer();
		String postResponse="";
		HttpURLConnection con=null;
		while(con==null || con.getResponseCode()!=200)
		{
			try {
				// Set up the URL and open a connection
				URL url = new URL("https://qm.banjaluka.rs.ba:443/api/services");
//			URL url = new URL("http://localhost:8080/api/services");
				con = (HttpURLConnection) url.openConnection();

				// Set the request method to POST
				con.setRequestMethod("GET");

				// Set the request content type to JSON
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Authorization", "Bearer "+jwtToken);

				if(con.getResponseCode()==401)
				{
					System.out.println("401");
					Thread.sleep(1000);
					postResponse=postRequest();
					if(postResponse=="exception")
					{
						return "exception";
					}
//					return getServicesRequest();
				}

				if(con.getResponseCode()==403)
				{
					Thread.sleep(1000);
					postResponse=postRequest();
					if(postResponse=="exception")
					{
						return "exception";
					}
					System.out.println("403");
//					return getServicesRequest();
				}
				// Read the response
				if(con.getResponseCode()==200)
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();
				}

			}
			catch (Exception e) {
				//ovdje vratiti poseban string koji cu prepoznati tamo u funkciji gdje zovem ovu
				//isti princip napraviti i za createNewAppointment, samo tamo ako je van sati onda ne raditi nista
				//za post request mala sansa da bude exception jer se on poziva samo pri pozivu ovih funckija
				//a to znaci da bi one prve izbacile exception
				e.printStackTrace();
				return "exception";
//			postRequest();
			}

		}
		return content.toString();
	}

	private String createNewAppointmentRequest(long serviceId) throws IOException {
		StringBuffer content = new StringBuffer();
		String postResponse="";
		HttpURLConnection con=null;

		while(con==null || con.getResponseCode()!=200)
		{
			try {
				// Set up the URL and open a connection
//			URL url = new URL("http://localhost:8080/api/appointments/new?service_id="+serviceId);
				URL url = new URL("https://qm.banjaluka.rs.ba:443/api/appointments/new?service_id="+serviceId);

				con = (HttpURLConnection) url.openConnection();

				// Set the request method to POST
				con.setRequestMethod("GET");

				// Set the request content type to JSON
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Authorization", "Bearer "+jwtToken);

				if(con.getResponseCode()==401 || con.getResponseCode()==403)
				{
					Thread.sleep(1000);
					postResponse=postRequest();
					if(postResponse=="exception")
					{
						return "exception";
					}
				}


				else if(con.getResponseCode()==400)
				{
					//van sati
					return "400";
				}

				if(con.getResponseCode()==200)
				{
					// Read the response
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();

				}

			} catch (Exception e) {
				e.printStackTrace();
				return "exception";
			}

		}

		return content.toString();

	}

	private Future<JWT> getJWTCallable()
	{
		Callable<JWT> task = () -> {
			String response=postRequest();
			return JSONParser.createJWTObject(response.toString());
		};
		return executorService.submit(task);
	}

	private void getJWT() {
		Thread thread = new Thread(){
			public void run() {
				try {
					jwtToken = getJWTCallable().get().getToken();

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

			};
		};
		thread.start();
	}


	private Future<List<Service>>getServices(){
		Callable<List<Service>> task = () -> {
			String response="exception";
			long milis=1000;
			while(response=="exception")
			{
				response=getServicesRequest();
				Thread.sleep(milis);
				if(milis<=64000)
				{
					milis*=2;
				}
			}
			return JSONParser.createServiceListObject(response);
		};
		
		return executorService.submit(task);
	}
	
	private Future<AppointmentInfoResponse>createNewAppointment(long serviceId){
		Callable<AppointmentInfoResponse> task = () -> {
			String response="exception";
			long milis=1000;
			while(response=="exception")
			{
				response = createNewAppointmentRequest(serviceId);
				Thread.sleep(milis);
				if(milis<=64000)
				{
					milis*=2;
				}
			}
			if(response=="400")
				return null;
			return JSONParser.createAppointmentInfoResponseObject(response);
		};
		
		return executorService.submit(task);
	}
	

	private void Timenow(){
	    Thread thread = new Thread(){
	    	public void run() {
		        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		        while(true){
		            try{
		                Thread.sleep(1000);
		            }catch(Exception e){
		                System.out.println(e);
		            }
		            final String timenow = sdf.format(new Date());
		            Platform.runLater(() -> {
		                 labelDateAndTime.setText(timenow); // This is the label
		                 Font font = Font.font("Times New Roman", FontWeight.NORMAL, 60);
		                 labelDateAndTime.setFont(font);
		            });
		        }

	    	};
	    };
	    thread.start();
	}

	private void getToken(boolean isInitialized)
	{
		Dialog<Boolean> dialog=new Dialog<Boolean>();
		//otvoriti dialog
		if(isInitialized)
		{
			Stage stage=(Stage)root.getScene().getWindow();
			dialog.setTitle("Učitavanje");
			dialog.initOwner(stage);
			stage.setAlwaysOnTop(true);
			dialog.show();
		}
		postRequestCall();
		if(isInitialized)
		{
			Platform.runLater(() ->
			{
				dialog.setResult(true);
				dialog.close();
			});
		}
		if(!isInitialized)
		{
			buttons= new Button[]{button1, button2, button3, button4};
			getAllServices(buttons);
		}
	}

	private void postRequestCall()
	{
		Thread thread = new Thread(){
			public void run() {
				String responseCode="";
				while(responseCode.equals("")){
					try{
						responseCode=postRequest();
					}catch(Exception e){
						System.out.println(e);
					}
					if(!responseCode.equals("200"))
					{
						Platform.runLater(() -> {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						});
					}
				}
			};
		};
		thread.start();

	}
	
	private String postRequest() throws IOException {
        StringBuffer content = new StringBuffer();
		HttpURLConnection con = null;

		while(con==null || con.getResponseCode()!=200)
		{
			try {
				// Set up the URL and open a connection
				URL url = new URL("https://qm.banjaluka.rs.ba:443/api/authenticate/external");
//			URL url = new URL("http://localhost:8080/api/authenticate/external");
				con = (HttpURLConnection) url.openConnection();

				// Set the request method to POST
				con.setRequestMethod("POST");

				// Set the request content type to JSON
				con.setRequestProperty("Content-Type", "application/json");

				// Set up the request body
				String requestBody = "{\"username\": \"ticketMachine\", \"password\": \"CkwMhvpgcdKAJp46Vvjbq7XrAxLGYKTa5XPfs4g\"}";
				con.setDoOutput(true);
				OutputStream os = con.getOutputStream();
				byte[] input = requestBody.getBytes("utf-8");
				os.write(input, 0, input.length);


				if(con.getResponseCode()==401)
				{
					System.out.println("401");
					Thread.sleep(1000);
				}

				else if(con.getResponseCode()==403)
				{
					Thread.sleep(1000);
					System.out.println("403");
				}

				// Read the response
				else if(con.getResponseCode()==HttpURLConnection.HTTP_OK)
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();

					JWT jwt=JSONParser.createJWTObject(content.toString());
					jwtToken=jwt.getToken();
					System.out.println(jwt.getSubject());
					return "200";
				}

				return String.valueOf(con.getResponseCode());

				// Print the response content
			} catch (Exception e) {
				e.printStackTrace();
				return "exception";
			}

		}


//		JWT jwt=JSONParser.createJWTObject(content.toString());
//		jwtToken=jwt.getToken();
		return "";
	}
	
	@FXML private void button1Clicked()
	{
		String buttonText= button1.getText();
		Service service = null;
		for(int i=0;i<services.size();i++)
		{
			if(services.get(i).getName()==buttonText)
			{
				service=services.get(i);
			}
		}

		//uraditi get new appointment
		onClick(service.getId());
	}
	
	@FXML private void button2Clicked()
	{
		String buttonText= button2.getText();
		Service service = null;
		for(int i=0;i<services.size();i++)
		{
			if(services.get(i).getName()==buttonText)
			{
				service=services.get(i);
			}
		}

		//uraditi get new appointment
		onClick(service.getId());
	}
	
	@FXML private void button3Clicked()
	{
		String buttonText= button3.getText();
		Service service = null;
		for(int i=0;i<services.size();i++)
		{
			if(services.get(i).getName()==buttonText)
			{
				service=services.get(i);
			}
		}

		//uraditi get new appointment
		onClick(service.getId());
	}
	
	@FXML private void button4Clicked()
	{
		String buttonText= button4.getText();
		Service service = null;
		for(int i=0;i<services.size();i++)
		{
			if(services.get(i).getName()==buttonText)
			{
				service=services.get(i);
			}
		}

		//uraditi get new appointment
		onClick(service.getId());

	}
	
	
	
}
