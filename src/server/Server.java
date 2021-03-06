package server;

import message.*;
import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * This is the server if the booking system, it takes commands from users and
 * pass the messages to client through socket.
 * 
 * @author Lu (B05602022)
 * @version 1.0
 * @since 2018-06-07
 *
 */
public class Server {

	HashMap<Integer, String> codeMap = new HashMap<Integer, String>();

	/**
	 * This function keeps listening for socket requests.
	 */
	public void listen() {
		try {
			ServerSocket server = new ServerSocket(3588);
			while (true) {
				System.out.println("listening");
				Socket socket = server.accept();
				socket.setSoTimeout(15000);
				AddThread add = new AddThread(socket);
				add.start();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Server error");
		}
	}

	/**
	 * This class is the thread that the server creates once it acquired a message
	 * from client.
	 * 
	 * @author USER
	 *
	 */
	class AddThread extends Thread {
		private Object msg;
		Socket socket;
		ObjectOutputStream os = null;
		ObjectInputStream is = null;

		/**
		 * This is the constructor.
		 * 
		 * @param sk
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		public AddThread(Socket sk) throws IOException, ClassNotFoundException {
			socket = sk;
		}

		/**
		 * This function, depending on the message received by the server, executes the
		 * user request and returns the result if any.
		 * 
		 * @param msg
		 *            The message received by the server.
		 * @return
		 */
		synchronized private Object messageHandler(Object msg) {
			Database database = new Database();
			try {
				if (msg.getClass() == new SearchCar().getClass()) {
					System.out.println("Server received SearchCar");
					SearchCar temp = (SearchCar) msg;
					if(temp.getTotal() > 5) {
						System.out.println("cannot order more than 5 tickets");
						return new Fail_Message("cannot order more than 5 tickets");
					}
						
					else if(!dateValid(temp.getDepartDay())) {
						System.out.println("cannot order tickets of this date");
						return new Fail_Message("cannot order tickets of this date");
					}	
					else {
						return database.selectCar((SearchCar) msg);
					}					
				} else if (msg.getClass() == new Order().getClass()) {
					System.out.println("Server received Order");
					int code;
					Random ran = new Random();
					do {
						code = ran.nextInt(1000000);
					} while (codeMap.get(code) != null);
					codeMap.put(code, "taken");
					System.out.println("Order start processing");
					return database.insertBooking((Order) msg, code);
				} else if (msg.getClass() == new SearchOrder().getClass()) {
					System.out.println("Server received SearchOrder");
					return database.searchTicketByUserId((SearchOrder) msg);
				} else if (msg.getClass() == new SearchTransactionNumber().getClass()) {
					System.out.println("Server received SearchTransactionNumber");
					return database.findTransactionNumber((SearchTransactionNumber) msg);
				} else if (msg.getClass() == new Ticket().getClass()) {
					System.out.println("Server received Ticket (for canceling)");
					return database.cancelTicket((Ticket) msg);
//				} else if (msg.getClass() == new SearchDaily().getClass()) {
//					System.out.println("Server received SearchDaily");
//					return database.searchDaily((SearchDaily) msg);
				} else {
					System.out.println("Server received w");
					return null;
				}
			} catch (Fail_Message e) {
				System.out.println("messageHandler error");
				System.out.println(e.getQuery());
				return e;
			}

		}
		/**
		 * This  function helps determine if the date is valid
		 * @param date
		 * @return
		 */
		private boolean dateValid(String date) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.now();
			String current = dtf.format(localDate); 
			String[] ccutted = current.split("-");
			int currentY = Integer.parseInt(ccutted[0]);
			int currentM = Integer.parseInt(ccutted[1]);
			int currentd = Integer.parseInt(ccutted[2]);
			String[] dcutted = date.split("-");
			int dateY = Integer.parseInt(dcutted[0]);
			int dateM = Integer.parseInt(dcutted[1]);
			int dated = Integer.parseInt(dcutted[2]);
			int flag = 0;
			if(dateY != currentY) 
				return false;
			else if(dateM > currentM)
				return false;
			else
				return true;
			
		} 

		/**
		 * This is the running function of the thread, it reads the object in the socket
		 * and pass it to messageHandler.
		 * 
		 */
		public void run() {
			try {
				os = new ObjectOutputStream(socket.getOutputStream());
				is = new ObjectInputStream(socket.getInputStream());
				this.msg = (Object) is.readObject();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
				System.out.println("input/output stream error");
			}
			Object returnMsg = messageHandler(msg);

			try {
				if (returnMsg == null)
					System.out.println("server send null object");
				else 
					System.out.println("server send" + returnMsg.getClass().toString());
				os.writeObject(returnMsg);
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("return message IO error");
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Server server = new Server();
		server.listen();
	}
}