package es.um.redes.nanoChat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.sun.xml.internal.bind.api.RawAccessor;

import es.um.redes.nanoChat.messageFV.NCInfoMessage;
import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCOpcodeMessage;
import es.um.redes.nanoChat.messageFV.NCRoomListMessage;
import es.um.redes.nanoChat.messageFV.NCRoomMessage;
import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;
import es.um.redes.nanoChat.server.roomManager.NCRoomManager;

/**
 * A new thread runs for each connected client
 */
public class NCServerThread extends Thread {
	
	private Socket socket = null;
	//Manager global compartido entre los Threads
	private NCServerManager serverManager = null;
	//Input and Output Streams
	private DataInputStream dis;
	private DataOutputStream dos;
	//Usuario actual al que atiende este Thread
	String user;
	//RoomManager actual (dependerá de la sala a la que entre el usuario)
	NCRoomManager roomManager;
	//Sala actual
	String currentRoom;

	//Inicialización de la sala
	public NCServerThread(NCServerManager manager, Socket socket) throws IOException {
		super("NCServerThread");
		this.socket = socket;
		this.serverManager = manager;
	}

	//Main loop
	public void run() {
		try {
			//Se obtienen los streams a partir del Socket
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			//En primer lugar hay que recibir y verificar el nick
			receiveAndVerifyNickname();
			//Mientras que la conexión esté activa entonces...
			while (true) {
				//TODO Obtenemos el mensaje que llega y analizamos su código de operación
				NCMessage message = NCMessage.readMessageFromSocket(dis);
				
				switch (message.getOpcode()) {
				//TODO 1) si se nos pide la lista de salas se envía llamando a sendRoomList();
					case NCMessage.OP_GET_ROOMLIST:
						sendRoomList();
						break;
				//TODO 2) Si se nos pide entrar en la sala entonces obtenemos el RoomManager de la sala,
				//TODO 2) notificamos al usuario que ha sido aceptado y procesamos mensajes con processRoomMessages()
				//TODO 2) Si el usuario no es aceptado en la sala entonces se le notifica al cliente
					case NCMessage.OP_ENTER_ROOM:
						NCRoomMessage message1 = (NCRoomMessage)message;
						String name = message1.getName();
						roomManager = serverManager.enterRoom(this.user, name, this.socket);
						currentRoom = name;
						NCOpcodeMessage response = (NCOpcodeMessage)NCMessage.makeOpcodeMessage(NCMessage.OP_ENTER_TRUE);
						String rawresponse = response.toEncodedString();
						dos.writeUTF(rawresponse);
						processRoomMessages();
				
				}
			}
		} catch (Exception e) {
			//If an error occurs with the communications the user is removed from all the managers and the connection is closed
			System.out.println("* User "+ user + " disconnected.");
			serverManager.leaveRoom(user, currentRoom);
			serverManager.removeUser(user);
		}
		finally {
			if (!socket.isClosed())
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}

	//Obtenemos el nick y solicitamos al ServerManager que verifique si está duplicado
	private void receiveAndVerifyNickname() throws IOException {
		//La lógica de nuestro programa nos obliga a que haya un nick registrado antes de proseguir
		//TODO Entramos en un bucle hasta comprobar que alguno de los nicks proporcionados no está duplicado
	    //TODO Extraer el nick del mensaje
		//TODO Validar el nick utilizando el ServerManager - addUser()
		boolean nickVerification = false;
		while(!nickVerification){
			NCRoomMessage message = (NCRoomMessage)NCMessage.readMessageFromSocket(dis);
			String nickname = message.getName();
			nickVerification = serverManager.addUser(nickname);
			if (nickVerification) {
				NCOpcodeMessage messageOK = (NCOpcodeMessage)NCMessage.makeOpcodeMessage(NCMessage.OP_NICK_OK);
				dos.writeUTF(messageOK.toEncodedString());
				user = nickname;
			}
			else {
				NCOpcodeMessage messageDuplicated = (NCOpcodeMessage)NCMessage.makeOpcodeMessage(NCMessage.OP_NICK_DUPLICADO);
				dos.writeUTF(messageDuplicated.toEncodedString());
			}
		}
		//TODO Contestar al cliente con el resultado (éxito o duplicado)
	}

	//Mandamos al cliente la lista de salas existentes
	private void sendRoomList() throws IOException  {
		ArrayList<NCRoomDescription> roomList = serverManager.getRoomList();
		NCRoomListMessage message = new NCRoomListMessage(NCMessage.OP_SEND_ROOMLIST, roomList);
		String stringmessage = message.toEncodedString();
		dos.writeUTF(stringmessage);
		//TODO La lista de salas debe obtenerse a partir del RoomManager y después enviarse mediante su mensaje correspondiente
	}

	private void processRoomMessages() throws IOException  {
		//TODO Comprobamos los mensajes que llegan hasta que el usuario decida salir de la sala
		boolean exit = false;
		while (!exit) {
			//TODO Se recibe el mensaje enviado por el usuario
			NCMessage mensaje = NCMessage.readMessageFromSocket(dis);
			byte code = mensaje.getOpcode();
			switch(code) {
			case NCMessage.OP_GET_ROOMINFO:
			{
				NCInfoMessage message = (NCInfoMessage)NCMessage.makeInfoMessage(NCMessage.OP_SEND_ROOMINFO, roomManager.getDescription());
				String rawresponse = message.toEncodedString();
				dos.writeUTF(rawresponse);
				break;
			}
			case NCMessage.OP_EXIT:
			{
				roomManager.removeUser(user);
				exit = true;
				break;
			}
			}
		}
	}
}
