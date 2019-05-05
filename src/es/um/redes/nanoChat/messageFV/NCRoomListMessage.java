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
		sb.append("RoomList"+DELIMITER+END_LINE); //Construimos el campo
		for(NCRoomDescription valores : roomList) {
			sb.append(valores.toPrintableString()+END_LINE);
		}
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
		
	}
	
	public static NCRoomListMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		int idx = lines[0].indexOf(DELIMITER); // Posición del delimitador
		int idxpar1 = lines[2].indexOf("(");
		int idxpar2 = lines[2].indexOf(")");
		String fieldRoomlist = lines[1].substring(0, idx-1).toLowerCase();
		ArrayList<NCRoomDescription> array = new ArrayList<NCRoomDescription>();
		for (int i = 2; i < lines.length; i++) {
			long timeLastMessage;
			int idxL = lines[i].indexOf("L");
			int idxM = lines[2].indexOf("M");
			String fieldRoomName = lines[i].substring(0, idx).toLowerCase();
			String valueRoomName = lines[i].substring(idx+2, idxM-2).trim();
			String fieldMembers = lines[i].substring(idxM, idxpar1).toLowerCase();
			String valueMembers = lines[i].substring(idxpar2+4, idxL-1).trim();
			String LongitudMembers = lines[i].substring(idxpar1+1, idxpar2).trim();
			String fieldLastMessage = lines[i].substring(idxL, idxL+12).toLowerCase();
			String valueLastMessage = lines[i].substring(idxL+14);
			String[] membersSplit = valueMembers.split("\\s*,\\s*");
			ArrayList<String> members = new ArrayList<String>();
			for(String m : membersSplit) {
				members.add(m);
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
