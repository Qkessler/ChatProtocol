package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

public class NCRoomListMessage extends NCMessage {
	
	private ArrayList<NCRoomDescription> roomList;
	private int longitudArray;
	
	public NCRoomListMessage(byte opcode, int longitudArray, ArrayList<NCRoomDescription> roomList) {
		this.opcode = opcode;
		this.longitudArray = longitudArray;
		this.roomList = roomList;
	}
	public ArrayList<NCRoomDescription> getRoomList() {
		return roomList;
	}
	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();			
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE); //Construimos el campo
		sb.append("Longitud"+DELIMITER+longitudArray+END_LINE);
		sb.append("RoomList"+DELIMITER+END_LINE); //Construimos el campo
		for(int i = 0; i < longitudArray; i++) {
			sb.append(roomList.get(i).toPrintableString()+END_LINE);
		}
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
		
	}
	
	public static NCRoomListMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		int idx = lines[1].indexOf(DELIMITER); // Posición del delimitador
		String fieldLongitud = lines[1].substring(0, idx).toLowerCase(); 																		// minúsculas
		String valueLongitud = lines[1].substring(idx + 1).trim();
		int longitud = 0;
		if (fieldLongitud.equalsIgnoreCase("Longitud")) {
			longitud = Integer.parseInt(valueLongitud);
		}
		String fieldRoomlist = lines[2].substring(0, DELIMITER).toLowerCase();
		ArrayList<NCRoomDescription> array = new ArrayList<NCRoomDescription>();
		for (int i = 0; i < longitud; i++) {
			
			array.add(NCRoomDescription.fromStringtoNCRoomDescription(
					lines[i+3].substring(END_LINE, END_LINE+1)));
		}
		return new NCRoomListMessage(code, longitud, array);
	}

}
