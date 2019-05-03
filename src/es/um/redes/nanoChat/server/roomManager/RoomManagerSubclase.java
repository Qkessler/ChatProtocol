package es.um.redes.nanoChat.server.roomManager;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomManagerSubclase extends NCRoomManager{
	private HashMap<String, Socket> miembros;
	private long tiempoUltimoMensaje;

	@Override
	public boolean registerUser(String u, Socket s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void broadcastMessage(String u, String message) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUser(String u) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRoomName(String roomName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NCRoomDescription getDescription() {
		ArrayList<String> nombreUsuarios = new ArrayList<String>();
		nombreUsuarios.addAll(miembros.keySet());
		return new NCRoomDescription(roomName, nombreUsuarios, tiempoUltimoMensaje);
	}

	@Override
	public int usersInRoom() {
		// TODO Auto-generated method stub
		return 0;
	}

}
