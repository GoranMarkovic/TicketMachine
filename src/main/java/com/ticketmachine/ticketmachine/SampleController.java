package com.ticketmachine.ticketmachine;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

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
		buttons= new Button[]{button1, button2, button3, button4};
		Platform.runLater(() ->
		{
			getAllServices(buttons);
		});
	}

	@FXML
	private Dialog<Boolean> createDialog(String message)
	{
		Stage stage=(Stage)root.getScene().getWindow();
		Dialog<Boolean> dialog=new Dialog<Boolean>();
		dialog.getDialogPane().setPrefSize(600,100);
		dialog.setTitle("QManager");
		Label contentLabel = new Label(message);
//		contentLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
		contentLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 32)); // set font size to 16
		contentLabel.setAlignment(Pos.CENTER);
		contentLabel.setTextFill(Color.WHITE);
		contentLabel.setTextAlignment(TextAlignment.CENTER);
		dialog.getDialogPane().setContent(contentLabel);
//		dialog.setContentText(message);
		dialog.initOwner(stage);
//		dialog.getDialogPane().setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
		stage.setAlwaysOnTop(true);
		dialog.show();
		return dialog;
	}
	
	private void getAllServices(Button[] buttons) {
		Dialog<Boolean> dialog=createDialog("Konektovanje sa serverom...");
		Thread thread = new Thread(){
	    	public void run() {
	    		try {
					services = getServices().get();

	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		} catch (ExecutionException e) {
	    			e.printStackTrace();
	    		}
				catch (Exception e)
				{
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
						dialog.setResult(true);
						dialog.close();
	            	}
	            });
		        

	    	};
	    };
	    thread.start();
	}
	
	@FXML
	private void onClick(long serviceId) {

		Dialog<Boolean> dialog=createDialog("Sačekajte listić...");
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

					else
					{
						Platform.runLater(() -> {
							Label contentLabel = new Label("Neuspješna rezervacija...");
							contentLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
							contentLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 32)); // set font size to 16
							contentLabel.setAlignment(Pos.CENTER);
							contentLabel.setTextAlignment(TextAlignment.CENTER);

							dialog.getDialogPane().setContent(contentLabel);
							dialog.show();
						});
					}


				} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		} catch (ExecutionException e) {
	    			e.printStackTrace();
	    		}
	    		
	            Platform.runLater(() -> {
					Timeline timeline=new Timeline(new KeyFrame(Duration.seconds(5), event ->{
						dialog.setResult(true);
						dialog.close();
					}));
					timeline.play();
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

				if(con.getResponseCode()==401 || con.getResponseCode()==403)
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
				e.printStackTrace();
				return "exception";
			}

		}
		return content.toString();
	}

	private String createNewAppointmentRequest(long serviceId) throws Exception {
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
				System.out.println("App code is "+con.getResponseCode());

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

				else if(con.getResponseCode()==200)
				{
					// Read the response
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();

				}

				else
					return "exception"; //neuspjesan pokusaj rezervacije

			} catch (Exception e) {
				e.printStackTrace();
				return "exception";
			}

		}
		System.out.println(content.toString());

		return content.toString();

	}

	private Future<List<Service>>getServices(){
		Callable<List<Service>> task = () -> {
			String response="exception";
			long milis=1000;
			while(response=="exception")
			{
				response=getServicesRequest();
				if(response=="exception")
					Thread.sleep(milis);
				if(milis<=20000)
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

			String response=createNewAppointmentRequest(serviceId);
			if(response=="exception" || response=="400")
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


	private String postRequest() throws Exception {
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

				System.out.println("Post code is "+con.getResponseCode());
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

				else
					return "exception"; //tretiramo kao neuspjesan pokusaj

				return String.valueOf(con.getResponseCode());

				// Print the response content
			} catch (Exception e) {
				e.printStackTrace();
				return "exception";
			}

		}

		return "exception"; // ne bi trebalo do ovoga da dodje ali za svaki slucaj tretirati kao neuspjesan pokusaj
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
		System.out.println("Size je " +services.size());

		for(int i=0;i<services.size();i++)
		{
			System.out.println(services.get(i).getName());
			System.out.println(buttonText);
			if(services.get(i).getName()==buttonText)
			{
				service=services.get(i);
			}
		}

		//uraditi get new appointment
		onClick(service.getId());

	}
	
	
	
}
