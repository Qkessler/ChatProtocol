package es.um.redes.nanoChat.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;
import es.um.redes.nanoChat.server.roomManager.NCRoomManager;

/**
 * Esta clase contiene el estado general del servidor (sin la lógica relacionada con cada sala particular)
 */
class NCServerManager {
	
	//Primera habitación del servidor
	final static byte INITIAL_ROOM = 'A';
	final static String ROOM_PREFIX = "Room";
	//Siguiente habitación que se creará
	byte nextRoom;
	//Usuarios registrados en el servidor
	private HashSet<String> users = new HashSet<String>();
	//Habitaciones actuales asociadas a sus correspondientes RoomManagers
	public HashMap<String,NCRoomManager> rooms = new HashMap<String,NCRoomManager>();
	
	NCServerManager() {
		nextRoom = INITIAL_ROOM;
	}
	
	//Método para registrar un RoomManager 
	public void registerRoomManager(NCRoomManager rm) {
		//TODO Dar soporte para que pueda haber más de una sala en el servidor
		String roomName = ROOM_PREFIX + (char) nextRoom; 
		rooms.put(roomName, rm);
	}
	
	//Devuelve la descripción de las salas existentes
	public synchronized ArrayList<NCRoomDescription> getRoomList() {
		//TODO Pregunta a cada RoomManager cuál es la descripción actual de su sala
		ArrayList <NCRoomDescription> arrayList = new ArrayList <NCRoomDescription>();
		for (NCRoomManager roommanager : rooms.values()) {
			System.out.println(roommanager.getDescription().toPrintableString());
			arrayList.add(roommanager.getDescription());
			
		}
		//TODO Añade la información al ArrayList
		return arrayList;
	}
	
	
	//Intenta registrar al usuario en el servidor.
	public synchronized boolean addUser(String user) {
		//DONE Devuelve true si no hay otro usuario con su nombre
		if (!users.contains(user)) {
			users.add(user);
			
			return true;
		}
		else {return false;}
		//DONE Devuelve false si ya hay un usuario con su nombre
	}
	
	//Elimina al usuario del servidor
	public synchronized void removeUser(String user) {
		//TODO Elimina al usuario del servidor
		if (users.contains(user)) {
			users.remove(user);
		}
	}
	
	//Un usuario solicita acceso para entrar a una sala y registrar su conexión en ella
	public synchronized NCRoomManager enterRoom(String u, String room, Socket s) {
		//TODO Verificamos si la sala existe
		if (rooms.containsKey(room)) {
			return rooms.get(room);
		}
		else {
			return null;
		}
		//TODO Decidimos qué hacer si la sala no existe (devolver error O crear la sala)
		//TODO Si la sala existe y si es aceptado en la sala entonces devolvemos el RoomManager de la sala
	}
	
	//Un usuario deja la sala en la que estaba 
	public synchronized void leaveRoom(String u, String room) {
		//TODO Verificamos si la sala existe
		if (rooms.containsKey(room)) {
			rooms.get(room).getDescription().members.remove(u);
		}
		//TODO Si la sala existe sacamos al usuario de la sala
		
		//Hemos decidido que bajo la condición de que la sala esté vacía, 
		//no vamos a hacer nada, vamos a trabajar con salas vacías.
		
		//TODO Decidir qué hacer si la sala se queda vacía
	}
}
