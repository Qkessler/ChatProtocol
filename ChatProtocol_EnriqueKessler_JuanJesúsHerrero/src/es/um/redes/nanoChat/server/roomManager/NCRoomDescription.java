package es.um.redes.nanoChat.server.roomManager;

import java.util.ArrayList;
import java.util.Date;

public class NCRoomDescription {
	//Campos de los que, al menos, se compone una descripción de una sala 
	public String roomName;
	public ArrayList<String> members;
	public int membersSize;
	public long timeLastMessage;
	
	//Constructor a partir de los valores para los campos
	public NCRoomDescription(String roomName, ArrayList<String> members, int size, long timeLastMessage) {
		this.roomName = roomName;
		this.members = members;
		this.membersSize = size;
		this.timeLastMessage = timeLastMessage;
	}
		
	//Método que devuelve una representación de la Descripción lista para ser impresa por pantalla
	public String toPrintableString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Room Name: "+roomName+"\t Members("+membersSize+ ") : ");
		for (String member: members) {
			sb.append(member+" ");
		}
		if (timeLastMessage != 0)
			sb.append("\tLast message: "+new Date(timeLastMessage).toString());
		else 
			sb.append("\tLast message: not yet");
		return sb.toString();
	}

}
