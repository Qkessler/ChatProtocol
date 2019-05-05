package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

public class NCInfoMessage extends NCMessage{
	private NCRoomDescription descripcion;
	
	public NCInfoMessage(byte code, NCRoomDescription descripcion) {
		this.opcode = code;
		this.descripcion = descripcion;
	}

	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE);
		sb.append("Info"+DELIMITER+END_LINE);
		sb.append(descripcion.toPrintableString()+END_LINE);
		sb.append(END_LINE);
		return sb.toString();
	}
	
	public static  NCInfoMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		int idx = lines[0].indexOf(DELIMITER); // Posición del delimitador
		int idxt = lines[2].indexOf("\t");
		int idxpar1 = lines[2].indexOf("(");
		int idxpar2 = lines[2].indexOf(")");
		int idxL = lines[2].indexOf("L");
		long timeLastMessage;
		String fieldRoomName = lines[2].substring(0, idx).toLowerCase();
		String valueRoomName = lines[2].substring(idx+1, idxt).trim();
		String fieldMembers = lines[2].substring(idxt+2, idxpar1).toLowerCase();
		String valueMembers = lines[2].substring(idxpar2+4, idxL-1).trim();
		String LongitudMembers = lines[2].substring(idxpar1+1, idxpar2).trim();
		String fieldLastMessage = lines[2].substring(idxL, idxL+12).toLowerCase();
		String valueLastMessage = lines[2].substring(idxL+14);
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
		NCRoomDescription descripcion1 = new NCRoomDescription(valueRoomName, members, Integer.parseInt(LongitudMembers), timeLastMessage);
	return new NCInfoMessage(code, descripcion1);
	}
	
	public NCRoomDescription getDescripcion() {
		return descripcion;
	}

}
