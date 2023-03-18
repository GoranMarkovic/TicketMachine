package com.ticketmachine.ticketmachine;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
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
	
	private Button[] buttons= {buttonRodjeni,buttonUmrli,buttonVjencani,buttonMaticnaSluzba};
	private List<Service> services;

	
	
	
	public void initialize()
	{
		Timenow();
		//poziv za prikaz servisa
		getAllServices();
	}
	
	private void getAllServices() {
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
	            		buttons[i+1].setText(tmp.getName());
	            		System.out.println(tmp.getName());
	            		System.out.println(tmp.getId());
	            	}
	            });
		        

	    	};
	    };
	    thread.start();
	}
	
	
	private void onClick(long serviceId) {
		//otvoriti dialog
		Thread thread = new Thread(){
	    	public void run() {
	    		try {
	    			AppointmentInfoResponse appointmentInfoResponse = createNewAppointment(serviceId).get();
	    			
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		} catch (ExecutionException e) {
	    			e.printStackTrace();
	    		}
	    		
	            Platform.runLater(() -> {
	                 //ovdje zatvoriti dialog
	            });
		        

	    	};
	    };
	    thread.start();
	}
	
	private Future<List<Service>>getServices(){
		Callable<List<Service>> task = () -> {
			String response = ApiRest.get("http://localhost:8080/api/services").get();
			return JSONParser.createServiceListObject(response);
		};
		
		return executorService.submit(task);
	}
	
	private Future<AppointmentInfoResponse>createNewAppointment(long serviceId){
		Callable<AppointmentInfoResponse> task = () -> {
			String response = ApiRest.get("http://localhost:8080/api/appointments/ticket-machine/new?service_id="+serviceId).get();
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
	
	private String getRequest(String url_string)
	{
        StringBuffer content = new StringBuffer();
        try {
            // Set up the URL and open a connection
            URL url = new URL(url_string);
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
		
		return content.toString();
	}
	
	@FXML private void rodjeniClicked()
	{
		//naci service id iz komponente
		onClick(1);
		
		
		//uzeti podatke sa servera
		String username="ticketMachine";
		String password="CkwMhvpgcdKAJp46Vvjbq7XrAxLGYKTa5XPfs4g";
		String ipAddress="192.168.1.6:8080/api/authenticate/external";
		String URL="http://"+ipAddress;
		String ret= getRequest(URL);
		System.out.println(ret);
		PrintClass pc=new PrintClass("Upis u matičnu knjigu rođenih", "6", "4", "B23");
		//pc.printNumber();
	}
	
	@FXML private void umrliClicked() 
	{
		//uzeti podatke sa servera
		PrintClass pc=new PrintClass("Upis u matičnu knjigu umrlih", "6", "4", "B23");
		System.out.println(counter);
		counter++;
		changeStateOfButtons();
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> changeStateOfButtons());
        pause.play();
		//pc.printNumber();
        Stage stage=(Stage)root.getScene().getWindow();
        Dialog<Boolean> dialog=new Dialog<Boolean>();
        dialog.setTitle("Sacekajte listic");
        dialog.initOwner(stage);
        stage.setAlwaysOnTop(true);
        dialog.show();
        
        PauseTransition dialogTransition= new PauseTransition(Duration.seconds(5));
        dialogTransition.setOnFinished(e-> dialog.setResult(true));
        dialogTransition.play();
        dialog.close();
	}
	
	@FXML private void vjencaniClicked()
	{
		//uzeti podatke sa servera
		PrintClass pc=new PrintClass("Upis u matičnu knjigu vjenčanih", "6", "4", "B23");
		//pc.printNumber();
	}
	
	@FXML private void maticnaSluzbaClicked()
	{
		PrintClass pc=new PrintClass("Upis u matičnu knjigu vjenčanih", "6", "4", "B23");
		//pc.printNumber();

	}
	
	
	
}
