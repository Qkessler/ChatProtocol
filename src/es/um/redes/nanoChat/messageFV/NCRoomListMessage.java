package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

public class NCRoomListMessage extends NCMessage {
	
	private ArrayList<NCRoomDescription> roomList;
	
	public NCRoomListMessage(byte opcode, ArrayList<NCRoomDescription> roomList) {
		this.opcode = opcode;
		this.roomList = roomList;
	}
	public ArrayList<NCRoomDescription> getRoomList() {
		return roomList;
	}
	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();			
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE); //Construimos el campo
		for(NCRoomDescription valores : roomList) {
			sb.append("Room Name: "+ valores.roomName+END_LINE);
			sb.append("Members length: "+ valores.membersSize+END_LINE);
			String membersstring = "Members: ";
			if(valores.membersSize == 0) {
				membersstring += "No existen miembros.";
			}
			else {	
			
			for(String x : valores.members) {
				membersstring += x;
				membersstring += " ";
			}
			}
			sb.append(membersstring+END_LINE);
			sb.append("Time Last Message: "+ Long.toString(valores.timeLastMessage)+END_LINE);
		}
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
		
	}
	
	public static NCRoomListMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		ArrayList<NCRoomDescription> array = new ArrayList<NCRoomDescription>();
		for (int i = 1; i < lines.length; i += 4) {
			long timeLastMessage;
			int idxL = lines[i].indexOf("T");
			int idxM = lines[i].indexOf("M");
			int idxpuntos1 = lines[i].indexOf(":"); // Posición del delimitador
			int idxpuntos2 = lines[i+1].indexOf(":");
			int idxpuntos3 = lines[i+2].indexOf(":");
			int idxpuntos4 = lines[i+3].indexOf(":");
			int idxpar1 = lines[i].indexOf("(");
			int idxpar2 = lines[i].indexOf(")");
			String fieldRoomName = lines[i].substring(0, idxpuntos1).toLowerCase();
			String valueRoomName = lines[i].substring(idxpuntos1+2).trim();
			String fieldMemberslength = lines[i+1].substring(0, idxpuntos2).toLowerCase();
			String LongitudMembers = lines[i+1].substring(idxpuntos2+2).trim();
			String fieldMembers = lines[i+2].substring(0, idxpuntos3).toLowerCase();
			String valueMembers = lines[i+2].substring(idxpuntos3+2).trim();
			String fieldLastMessage = lines[i+3].substring(0, idxpuntos4).toLowerCase();
			String valueLastMessage = lines[i+3].substring(idxpuntos4+2);
			String[] membersSplit = valueMembers.split("\\s*\\s\\s*");
			ArrayList<String> members = new ArrayList<String>();
			if (!valueMembers.equals("")) {
				for(String m : membersSplit) {
					members.add(m);
				}
			}
			if(valueLastMessage.equals("not yet")) {
				timeLastMessage = 0;
			}
			else {
				timeLastMessage = Long.parseLong(valueLastMessage);
			}
			array.add(new NCRoomDescription(valueRoomName, members, Integer.parseInt(LongitudMembers), timeLastMessage));
		}
		return new NCRoomListMessage(code, array);
	}

}
