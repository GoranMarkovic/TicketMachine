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
	@FXML private Button buttonRodjeni;
	@FXML private Button buttonUmrli;
	@FXML private Button buttonVjencani;
	@FXML private Button buttonMaticnaSluzba;
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
	private static String jwtToken;
	@FXML
	public void initialize()
	{
		Timenow();
		//poziv za prikaz servisa
//		getAllServices();
		postRequest("as");
//		jwtToken=postRequest("url");
		buttons= new Button[]{buttonRodjeni, buttonUmrli, buttonVjencani, buttonMaticnaSluzba};
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
		dialog.setTitle("Sacekajte listic");
		dialog.initOwner(stage);
		stage.setAlwaysOnTop(true);
		dialog.show();


		Thread thread = new Thread(){
	    	public void run() {
	    		try {
	    			AppointmentInfoResponse appointmentInfoResponse = createNewAppointment(serviceId).get();
					Appointment appointment=appointmentInfoResponse.getAppointment();
					String clientsInFront=Integer.toString(appointmentInfoResponse.getClientsInFront());
//					PrintClass pc=new PrintClass(appointment.getService().getName(), appointment.getService().getOfficeName(),clientsInFront,appointment.getTag());

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
			URL url = new URL("http://10.99.156.187:8080/api/services");
//			URL url = new URL("http://localhost:8080/api/services");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// Set the request method to POST
			con.setRequestMethod("GET");

			// Set the request content type to JSON
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+jwtToken);
			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			// Print the response content
			System.out.println(content.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	private String createNewAppointmentRequest(long serviceId)
	{
		StringBuffer content = new StringBuffer();
		try {
			// Set up the URL and open a connection
//			URL url = new URL("http://localhost:8080/api/appointments/ticket-machine/new?service_id="+serviceId);
			URL url = new URL("http://10.99.156.187:8080/api/appointments/new?service_id="+serviceId);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// Set the request method to POST
			con.setRequestMethod("GET");

			// Set the request content type to JSON
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+jwtToken);
			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			// Print the response content
			System.out.println(content.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();

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
			System.out.println(response);
			return JSONParser.createAppointmentInfoResponseObject(response);
		};
		
		return executorService.submit(task);
	}
	
	private void changeStateOfButtons()
	{
		if(buttonRodjeni.isDisabled())
		{
			buttonRodjeni.setDisable(false);
			buttonUmrli.setDisable(false);
			buttonVjencani.setDisable(false);
			buttonMaticnaSluzba.setDisable(false);
		}
		
		else
		{
			buttonRodjeni.setDisable(true);
			buttonUmrli.setDisable(true);
			buttonVjencani.setDisable(true);
			buttonMaticnaSluzba.setDisable(true);
		}
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
	
	private String postRequest(String url_string)
	{
        StringBuffer content = new StringBuffer();
        try {
            // Set up the URL and open a connection
            URL url = new URL("http://10.99.156.187:8080/api/authenticate/external");
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

            // Print the response content
            System.out.println(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
		JWT jwt=JSONParser.createJWTObject(content.toString());
		jwtToken=jwt.getToken();
		return content.toString();
	}
	
	@FXML private void rodjeniClicked()
	{
		//naci service id iz komponente
//		onClick(1);
		String buttonText=buttonRodjeni.getText();
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
		//procitati odgovor

//		PrintClass pc=new PrintClass("Upis u matičnu knjigu rođenih", "6", "4", "B23");
		//pc.printNumber();
	}
	
	@FXML private void umrliClicked() 
	{
		//uzeti podatke sa servera
//		PrintClass pc=new PrintClass("Upis u matičnu knjigu umrlih", "6", "4", "B23");
//		System.out.println(counter);
//		counter++;
//		changeStateOfButtons();
//        PauseTransition pause = new PauseTransition(Duration.seconds(5));
//        pause.setOnFinished(e -> changeStateOfButtons());
//        pause.play();
//		//pc.printNumber();
//        Stage stage=(Stage)root.getScene().getWindow();
//        Dialog<Boolean> dialog=new Dialog<Boolean>();
//        dialog.setTitle("Sacekajte listic");
//        dialog.initOwner(stage);
//        stage.setAlwaysOnTop(true);
//        dialog.show();
//
//        PauseTransition dialogTransition= new PauseTransition(Duration.seconds(5));
//        dialogTransition.setOnFinished(e-> dialog.setResult(true));
//        dialogTransition.play();
//        dialog.close();
		String buttonText=buttonUmrli.getText();
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
	
	@FXML private void vjencaniClicked()
	{
		//uzeti podatke sa servera
		//pc.printNumber();
		String buttonText=buttonVjencani.getText();
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
	
	@FXML private void maticnaSluzbaClicked()
	{
		//pc.printNumber();
		String buttonText=buttonMaticnaSluzba.getText();
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
