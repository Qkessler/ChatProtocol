package es.um.redes.nanoChat.server.roomManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

public class NCRoomDescription {
	//Campos de los que, al menos, se compone una descripción de una sala 
	public String roomName;
	public ArrayList<String> members;
	public long timeLastMessage;
	
	//Constructor a partir de los valores para los campos
	public NCRoomDescription(String roomName, ArrayList<String> members, long timeLastMessage) {
		this.roomName = roomName;
		this.members = members;
		this.timeLastMessage = timeLastMessage;
	}
		
	//Método que devuelve una representación de la Descripción lista para ser impresa por pantalla
	public String toPrintableString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Room Name: "+roomName+"\t Members ("+members.size()+ ") : ");
		for (String member: members) {
			sb.append(member+" ");
		}
		if (timeLastMessage != 0)
			sb.append("\tLast message: "+new Date(timeLastMessage).toString());
		else 
			sb.append("\tLast message: not yet");
		return sb.toString();
	}
	
	public static NCRoomDescription fromStringtoNCRoomDescription(String message) {
		ArrayList<String> miembros = new ArrayList<String>();
		String[] lines = message.split(System.getProperty("line.separator"));
		int idxt = lines[1].indexOf("\t");
		int idx = lines[1].indexOf(":");
		int idxp1 = lines[1].indexOf("(");
		int idxp2 = lines[1].indexOf(")");
		String fieldRoomName = lines[1].substring(0, idx);
		String valueRoomName = lines[1].substring(idx+1, idxt);
		String membersfield = lines[1].substring(idxt+1, idxp1);
		String sizevalue = lines[1].substring(idxp1+1, idxp2);
		for(int i = 0; i < Integer.parseInt(sizevalue); i++) {
			miembros.add(lines[1].substring(idx+2, idxt+1));
		}
		String fieldLastMessage = lines[1].substring(idxt+2, idx+3);
		String valueLastMessage = lines[1].substring(idx+3);
		long time = Long.parseLong(valueLastMessage);
		return new NCRoomDescription(valueRoomName, miembros, time);
		
	}
}
