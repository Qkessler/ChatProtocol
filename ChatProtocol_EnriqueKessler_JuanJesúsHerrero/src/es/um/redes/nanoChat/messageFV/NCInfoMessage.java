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
		sb.append("Room Name: "+ descripcion.roomName+END_LINE);
		sb.append("Members length: "+ descripcion.membersSize+END_LINE);
		String membersstring = "Members: ";
		if(descripcion.membersSize == 0) {
			membersstring += "No existen miembros.";
		}
		else {	
			for(String x : descripcion.members) {
				membersstring += x;
				membersstring += " ";
			}
		}
		sb.append(membersstring+END_LINE);
		sb.append("Time Last Message: "+ Long.toString(descripcion.timeLastMessage)+END_LINE);
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
	}
	
	public static  NCInfoMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		long timeLastMessage;
		int idxpuntos1 = lines[1].indexOf(":"); // Posición del delimitador
		int idxpuntos2 = lines[1+1].indexOf(":");
		int idxpuntos3 = lines[1+2].indexOf(":");
		int idxpuntos4 = lines[1+3].indexOf(":");
		String fieldRoomName = lines[1].substring(0, idxpuntos1).toLowerCase();
		String valueRoomName = lines[1].substring(idxpuntos1+2).trim();
		String fieldMemberslength = lines[1+1].substring(0, idxpuntos2).toLowerCase();
		String LongitudMembers = lines[1+1].substring(idxpuntos2+2).trim();
		String fieldMembers = lines[1+2].substring(0, idxpuntos3).toLowerCase();
		String valueMembers = lines[1+2].substring(idxpuntos3+2).trim();
		String fieldLastMessage = lines[1+3].substring(0, idxpuntos4).toLowerCase();
		String valueLastMessage = lines[1+3].substring(idxpuntos4+2);
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
		NCRoomDescription descripcion1 = new NCRoomDescription(valueRoomName, members, Integer.parseInt(LongitudMembers), timeLastMessage);
	return new NCInfoMessage(code, descripcion1);
	}
	
	public NCRoomDescription getDescripcion() {
		return descripcion;
	}

}
