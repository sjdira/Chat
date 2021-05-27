package Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServeurChat extends Thread {
	private boolean isActive = true;
	private int nombreClients;
	private List<Conversation> clients = new ArrayList<Conversation>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ServeurChat().start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket ss = new ServerSocket(1234);
			System.out.println("Demarrage du serveur ...");
			while (isActive) {
				Socket socket = ss.accept();
				++nombreClients;
				Conversation conversation = new Conversation(socket, nombreClients);
				clients.add(conversation);
				conversation.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class Conversation extends Thread {
		protected Socket socketClient;
		private int numeroClient;
		
		public Conversation(Socket socketClient, int numeroClient) {
			// TODO Auto-generated method stub
			this.socketClient = socketClient;
			this.numeroClient = numeroClient;
		}
		
		public void broadcastMessage(String message, Socket socket, int numClient) {
			try {
				for(Conversation client : clients) {
					if (client.socketClient != socket) {
						if(client.numeroClient == numClient || numClient == -1) {
							OutputStream os = client.socketClient.getOutputStream();
							PrintWriter pw = new PrintWriter(os,true);
							pw.println(message);
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void connectedUser(int numClient, Socket socket) {
			// TODO Auto-generated method stub
			if(nombreClients != 1) {
				String usrConnect = "Connected Users : [ ";
				for(Conversation client : clients) {
					if (client.numeroClient != numClient) {
						usrConnect = usrConnect + client.numeroClient + " ,";
					}
				}
				
				usrConnect = usrConnect.replaceFirst(".$"," ]");
			
				try {
					OutputStream os = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(os,true);
					pw.println(usrConnect);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				InputStream is = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socketClient.getOutputStream();
				PrintWriter pw = new PrintWriter(os,true);
				String IP = socketClient.getRemoteSocketAddress().toString();
				System.out.println("Connexion du client numero " + numeroClient + " IP " + IP);
				
				pw.println("Bienvenue vous etes le client numero " + numeroClient);
				connectedUser(numeroClient, socketClient);
				broadcastMessage("New user Connected, son numero est : " + numeroClient, socketClient, -1);
				
				while(true) {
					String req = br.readLine();
					if(req.contains(":")) {
						String[] requestParams = req.split(":");
						if(requestParams.length == 2);
						String message = requestParams[1];
						int numClient = Integer.parseInt(requestParams[0]);
						broadcastMessage("[ " +  DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()) + " " +message, socketClient, numClient);
					}
					else {
						broadcastMessage("[ " +  DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()) + " " + req, socketClient, -1);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
