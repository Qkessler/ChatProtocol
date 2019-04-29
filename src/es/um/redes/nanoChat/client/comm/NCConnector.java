package es.um.redes.nanoChat.client.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import es.um.redes.nanoChat.messageFV.NCInfoMessage;
import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCOpcodeMessage;
import es.um.redes.nanoChat.messageFV.NCRoomListMessage;
import es.um.redes.nanoChat.messageFV.NCRoomMessage;
import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor de NanoChat
public class NCConnector {
	private Socket socket;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	
	public NCConnector(InetSocketAddress serverAddress) throws UnknownHostException, IOException {
		// Se crea el socket a partir de la dirección proporcionada 
		socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
		// Se extraen los streams de entrada y salida
		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());
	}


	//Método para registrar el nick en el servidor. Nos informa sobre si la inscripción se hizo con éxito o no.
	public boolean registerNickname_UnformattedMessage(String nick) throws IOException {
		//Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
		// Enviamos una cadena con el nick por el flujo de salida
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		String cadena = "texto";
		dos.writeUTF(cadena);
		// Leemos la cadena recibida como respuesta por el flujo de entrada 
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		String cadenarecibida = dis.readUTF();
		// Si la cadena recibida es NICK_OK entonces no está duplicado (en función de ello modificar el return)
		if (cadenarecibida == "NICK_OK") {
			return true;
		}
		else {return false;}
	}

	
	//Método para registrar el nick en el servidor. Nos informa sobre si la inscripción se hizo con éxito o no.
	public boolean registerNickname(String nick) throws IOException {
		//Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
		//Creamos un mensaje de tipo RoomMessage con opcode OP_NICK en el que se inserte el nick
		NCRoomMessage message = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_NICK, nick);
		//Obtenemos el mensaje de texto listo para enviar
		String rawMessage = message.toEncodedString();
		//Escribimos el mensaje en el flujo de salida, es decir, provocamos que se envíe por la conexión TCP
		dos.writeUTF(rawMessage);
		// Leemos el mensaje recibido como respuesta por el flujo de entrada 
		String cadena = dis.readUTF();
		// Analizamos el mensaje para saber si está duplicado el nick (modificar el return en consecuencia)
		if (cadena.equals("NICK_DUPLICATED")) {
			System.out.println("El nick está duplicado");
			return false;
		}
		else {return true;}
	}
	
	//Método para obtener la lista de salas del servidor
	public ArrayList<NCRoomDescription> getRooms() throws IOException {
		//Funcionamiento resumido: SND(GET_ROOMS) and RCV(ROOM_LIST)
		// completar el método
		NCOpcodeMessage message = (NCOpcodeMessage) NCMessage.makeOpcodeMessage(NCMessage.OP_GET_ROOMLIST);
		String rawMessage = message.toEncodedString();
		dos.writeUTF(rawMessage);
		NCRoomListMessage roomList = (NCRoomListMessage)NCMessage.readMessageFromSocket(dis);
		return roomList.getRoomList();	
		//va a recibir ahora un mensaje con el opcode GET_ROOMLIST, en el que esta contenida la 
		//lista de NCRoomDescripcion.

	}
	
	//Método para solicitar la entrada en una sala
	public boolean enterRoom(String room) throws IOException {
		//Funcionamiento resumido: SND(ENTER_ROOM<room>) and RCV(IN_ROOM) or RCV(REJECT)
		NCRoomMessage enterRoomMessage = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_ENTER_ROOM, room);
		String raw = enterRoomMessage.toEncodedString();
		dos.writeUTF(raw);
		String response = dis.readUTF();
		if (response  == "IN_ROOM") {
			return true;
		}
		else return false;
		//TODO completar el método
		
	}
	
	//Método para salir de una sala
	public void leaveRoom(String room) throws IOException {
		//Funcionamiento resumido: SND(EXIT_ROOM)
		//TODO completar el método
	}
	
	//Método que utiliza el Shell para ver si hay datos en el flujo de entrada
	public boolean isDataAvailable() throws IOException {
		return (dis.available() != 0);
	}
	
	//IMPORTANTE!!
	//TODO Es necesario implementar métodos para recibir y enviar mensajes de chat a una sala
	
	//Método para pedir la descripción de una sala
	public NCRoomDescription getRoomInfo(String room) throws IOException {
		//Funcionamiento resumido: SND(GET_ROOMINFO) and RCV(ROOMINFO)
		//TODO Construimos el mensaje de solicitud de información de la sala específica
		NCRoomMessage messageEnviado = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_GET_ROOMINFO, room);
		String messageToString = messageEnviado.toEncodedString();
		dos.writeUTF(messageToString);
		NCInfoMessage message = (NCInfoMessage)NCMessage.readMessageFromSocket(dis);
		NCRoomDescription descripcion = message.getDescripcion();
		//TODO Recibimos el mensaje de respuesta
		//TODO Devolvemos la descripción contenida en el mensaje
		return descripcion;
	}
	
	//Método para cerrar la comunicación con la sala
	//TODO (Opcional) Enviar un mensaje de salida del servidor de Chat
	public void disconnect() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		} finally {
			socket = null;
		}
	}


}

//Example commit.
