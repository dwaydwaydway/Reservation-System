
import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;

public class CounterServer {
	public enum station {
		Nangang, Taipei, Banqiao, Taoyuan, Hsinchu, Miaoli, 
		Taichung, Tainan, Changhua, Yunlin, Chiayi, Zuoying
	}
	public enum type {
		normal, child, elder, disable
	}
	SearchCar SearchCar;
	
	public void listen() throws ClassNotFoundException {
		try {
			ServerSocket server = new ServerSocket(3335);
			while (true) {
				System.out.println("�����s�u��");
				Socket socket = server.accept();
				socket.setSoTimeout(15000);
				AddThread add = new AddThread(socket);
				add.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("���A����X�J�o�Ϳ��~");
		}
	}

	class AddThread extends Thread {
		private Messenger msg;
		ObjectInputStream inStream = null;
		public AddThread(Socket sk) throws IOException, ClassNotFoundException {
			ObjectInputStream inStream = new ObjectInputStream(sk.getInputStream());
			this.msg = (Messenger)inStream.readObject();
		}

		public void run() {
			if(msg.getClass() == SearchCar.getClass()) {
				SearchCar = (SearchCar)msg;
			}
			System.out.println(msg.getString);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		CounterServer counter = new CounterServer();
		counter.listen();
	}
}