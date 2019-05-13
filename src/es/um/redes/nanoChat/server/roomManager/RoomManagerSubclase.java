package es.um.redes.nanoChat.server.roomManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCSendMessage;

public class RoomManagerSubclase extends NCRoomManager{
	private HashMap<String, Socket> miembros = new HashMap<String, Socket>();
	private ArrayList<NCSendMessage> historial = new ArrayList<NCSendMessage>();
	private long tiempoUltimoMensaje;

	@Override
	public boolean registerUser(String u, Socket s) {
		miembros.put(u, s);
		return true;
	}
	@Override
	public void broadcastMessage(String u, String message) throws IOException {
		tiempoUltimoMensaje = System.currentTimeMillis();
		for(String e : miembros.keySet()) {
			if (!e.equals(u)) {
				Socket socket = miembros.get(e);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				NCSendMessage mensaje = (NCSendMessage)NCMessage.makeSendMessage(NCMessage.OP_SEND_CHAT, u, message);
				historial.add(mensaje);
				String raw = mensaje.toEncodedString();
				dos.writeUTF(raw);
			}
		}
		
	}

	@Override
	public void removeUser(String u) {
		miembros.remove(u);
		
	}

	@Override
	public void setRoomName(String roomName) {
		this.roomName = roomName;
		
	}
//	public ArrayList<NCSendMessage> getHistorial(){
//		return historial;
//	}

	@Override
	public NCRoomDescription getDescription() {
		ArrayList<String> nombreUsuarios = new ArrayList<>();
		nombreUsuarios.addAll(miembros.keySet());
		return new NCRoomDescription(roomName, nombreUsuarios, nombreUsuarios.size(), tiempoUltimoMensaje);
	}

	@Override
	public int usersInRoom() {
		return miembros.size();
	}

}
