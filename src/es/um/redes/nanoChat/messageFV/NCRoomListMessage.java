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
	
	@SuppressWarnings("deprecation")
	public static NCRoomListMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		int idx = lines[1].indexOf(DELIMITER); // Posición del delimitador
		int idxt = lines[3].indexOf("\t");
		int idxpar1 = lines[3].indexOf("(");
		int idxpar2 = lines[3].indexOf(")");
		int idxL = lines[3].indexOf("L");
		String fieldRoomlist = lines[2].substring(0, idx).toLowerCase();
		ArrayList<NCRoomDescription> array = new ArrayList<NCRoomDescription>();
		for (int i = 3; i < lines.length+1; i++) {
			long timeLastMessage;
			
			String fieldRoomName = lines[i].substring(0, idx-1).toLowerCase();
			String valueRoomName = lines[i].substring(idx+1, idxt-1).trim();
			String fieldMembers = lines[i].substring(idxt+2, idxpar1-1).toLowerCase();
			String LongitudMembers = lines[i].substring(idxpar1+1, idxpar2-1).trim();
			String fieldLastMessage = lines[i].substring(idxL, idxL+11).toLowerCase();
			String valueLastMessage = lines[i].substring(idxL+13);
			ArrayList<String> members = (ArrayList<String>) Arrays.asList(fieldMembers.split("\\s*,\\s*"));
			if(valueLastMessage =="not yet") {
				timeLastMessage = 0;
			}
			else {
				timeLastMessage = Long.parseLong(valueLastMessage);
			}
			array.add(new NCRoomDescription(valueRoomName, members, timeLastMessage));
		}
		return new NCRoomListMessage(code, array);
	}

}
