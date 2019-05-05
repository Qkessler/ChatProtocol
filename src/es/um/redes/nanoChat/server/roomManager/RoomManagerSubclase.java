package es.um.redes.nanoChat.server.roomManager;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomManagerSubclase extends NCRoomManager{
	private HashMap<String, Socket> miembros = new HashMap<String, Socket>();
	private long tiempoUltimoMensaje;

	@Override
	public boolean registerUser(String u, Socket s) {
		miembros.put(u, s);
		return true;
	}

	@Override
	public void broadcastMessage(String u, String message) throws IOException {
		
		
	}

	@Override
	public void removeUser(String u) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRoomName(String roomName) {
		this.roomName = roomName;
		
	}

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
