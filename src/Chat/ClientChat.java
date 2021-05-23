package Chat;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFileChooser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientChat extends Application{
	
	PrintWriter pw;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Client Chat");
		primaryStage.getIcons().add(new Image("/Icon/chat-professional.png"));
		
		BorderPane borderPane = new BorderPane();
		
		Label labelHost = new Label("Host:");
		TextField textFieldHost = new TextField("localhost");
		Label labelPort = new Label("Port:");
		TextField textFieldPort = new TextField("1234");
		Label labelNom = new Label("Nom:");
		TextField textFieldNom = new TextField("");
		Button buttonConnecter = new Button("Connecter");
		
		labelHost.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
		labelPort.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
		labelNom.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
		buttonConnecter.setFont(Font.font("Verdana", FontPosture.ITALIC, 16));
		
		HBox hBox = new HBox();
		hBox.setSpacing(10);
		hBox.setPadding(new Insets(10));
		hBox.setBackground(new Background(new BackgroundFill(Color.rgb(228,228,252),null,null)));
		hBox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFieldPort, labelNom, textFieldNom,buttonConnecter);
		borderPane.setTop(hBox);
		
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		vBox.setPadding(new Insets(10));
		ObservableList<String> listModel = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(listModel);
		vBox.getChildren().add(listView);
		
		BorderPane borderPane2 = new BorderPane();
		BorderPane borderPane3 = new BorderPane();
		
		final URL imageURL = getClass().getResource("/Icon/chat-professional.png"); 
        final Image image = new Image(imageURL.toExternalForm()); 
        final ImageView imageView = new ImageView(image);
        
        VBox vBox2 = new VBox();
		vBox2.setSpacing(10);
		vBox2.setPadding(new Insets(10));
		vBox2.setBackground(new Background(new BackgroundFill(Color.ORANGE,null,null)));
		
		Label labelMsg = new Label("Message:");
		TextField textFieldMsg = new TextField();
		textFieldMsg.setPrefSize(600, 40);
		Button buttonEnvoyer = new Button("Envoyer");
		Button buttonJoindre = new Button("Joindre");
		
		HBox hBox2 = new HBox();
		hBox2.setSpacing(10);
		hBox2.setPadding(new Insets(10));
		hBox2.getChildren().addAll(labelMsg,textFieldMsg,buttonEnvoyer,buttonJoindre);
		
		borderPane3.setCenter(vBox);
		borderPane3.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE,null,null)));
		
		borderPane3.setBottom(hBox2);
		
		Scene scene = new Scene(borderPane,1100,600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonConnecter.setOnAction((evt)-> {
			if(!textFieldNom.getText().isEmpty() && textFieldPort.getText().equals("1234") && textFieldHost.getText().equals("localhost")) {
				borderPane.setCenter(borderPane3);
				
				primaryStage.setTitle("Client Chat " + textFieldNom.getText());
				
				Label Nom = new Label("Nom : " + textFieldNom.getText());
				
				Nom.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
				
				borderPane2.setCenter(Nom);
				
				vBox2.getChildren().addAll(imageView, borderPane2);
				borderPane.setLeft(vBox2);
				
				String host = textFieldHost.getText();
				int port = Integer.parseInt(textFieldPort.getText());
				try {
					Socket socket = new Socket(host,port);
					InputStream inputStream = socket.getInputStream();
					InputStreamReader isr = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(isr);
					
					OutputStream outputStream = socket.getOutputStream();
					pw = new PrintWriter(outputStream, true);
					
					new Thread(()->{
						while(true) {
							try {
								String response = bufferedReader.readLine();
								Platform.runLater(()->{
									listModel.add(response);
								});
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.initStyle(StageStyle.UTILITY);
				alert.setTitle("Error Message");
				alert.setHeaderText(null);
				alert.setContentText("Merci de remplir le Host, le Port et Votre Nom pour se connecter au Serveur!");
				alert.showAndWait();
			}
			
		});
		
		buttonEnvoyer.setOnAction((evt)->{
			String msg = textFieldMsg.getText();
			String nom = textFieldNom.getText();
			if(msg.contains(":")) {
				String[] requestParams = msg.split(":");
				if(requestParams.length == 2);
				String message = requestParams[1];
				int numClient = Integer.parseInt(requestParams[0]);
				pw.println(numClient+":"+nom + " ] " + message);
			}
			else {
				pw.println(nom + " ] " + msg);
			}	
		});
		
		buttonJoindre.setOnAction((evt)->{
			FileChooser fc = new FileChooser();
			
			fc.setTitle("Open Resource File");
			fc.showOpenDialog(primaryStage);
			
		});
	}

}
