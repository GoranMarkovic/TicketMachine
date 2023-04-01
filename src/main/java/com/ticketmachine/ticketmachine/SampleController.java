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

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
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

	private String getServicesRequest()
	{
		StringBuffer content = new StringBuffer();
		try {
			// Set up the URL and open a connection
			URL url = new URL("https://qm.banjaluka.rs.ba:443/api/services");
//			URL url = new URL("http://localhost:8080/api/services");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

			// Set the request method to POST
			con.setRequestMethod("GET");

			// Set the request content type to JSON
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+jwtToken);

			if(con.getResponseCode()==401)
			{
				System.out.println("401");
				postRequest();
				return getServicesRequest();
			}

			if(con.getResponseCode()==403)
			{
				postRequest();
				System.out.println("403");
				Thread.sleep(30000);
				return getServicesRequest();
			}
			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
			postRequest();
		}
		return content.toString();
	}

	private String createNewAppointmentRequest(long serviceId)
	{
		StringBuffer content = new StringBuffer();
		try {
			// Set up the URL and open a connection
//			URL url = new URL("http://localhost:8080/api/appointments/new?service_id="+serviceId);
			URL url = new URL("https://qm.banjaluka.rs.ba:443/api/appointments/new?service_id="+serviceId);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// Set the request method to POST
			con.setRequestMethod("GET");

			// Set the request content type to JSON
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+jwtToken);

			if(con.getResponseCode()==401)
			{
				System.out.println("401");
				postRequest();
				return createNewAppointmentRequest(serviceId);
			}

			else if(con.getResponseCode()==403)
			{
				postRequest();
				System.out.println("403");
				Thread.sleep(30000);
				return createNewAppointmentRequest(serviceId);
			}

			else if(con.getResponseCode()==400)
			{
				return "400";
			}


			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();

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
			String response = getServicesRequest();
			return JSONParser.createServiceListObject(response);
		};
		
		return executorService.submit(task);
	}
	
	private Future<AppointmentInfoResponse>createNewAppointment(long serviceId){
		Callable<AppointmentInfoResponse> task = () -> {
			String response = createNewAppointmentRequest(serviceId);
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
	
	private String postRequest()
	{
        StringBuffer content = new StringBuffer();
        try {
            // Set up the URL and open a connection
            URL url = new URL("https://qm.banjaluka.rs.ba:443/api/authenticate/external");
//			URL url = new URL("http://localhost:8080/api/authenticate/external");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

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

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
			if(con.getResponseCode()==HttpURLConnection.HTTP_OK)
			{
				JWT jwt=JSONParser.createJWTObject(content.toString());
				jwtToken=jwt.getToken();
				System.out.println(jwt.getSubject());
			}
			else
			{
				Thread.sleep(30000);
				return postRequest();
			}
			return String.valueOf(con.getResponseCode());

            // Print the response content
        } catch (Exception e) {
			try {
				Thread.sleep(30000);
				return postRequest();
			}
			catch (Exception ex){

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
