package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;

public class NCHistoryMessage extends NCMessage{
	private ArrayList<NCSendMessage> messages;
	
	static private final String NAME_FIELD = "name";
	
	public NCHistoryMessage(byte opcode, ArrayList<NCSendMessage> messages) {
		this.opcode = opcode;
		this.messages = messages;
	}
	public ArrayList<NCSendMessage> getRoomList() {
		return messages;
	}
	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();			
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE); //Construimos el campo
		for(NCSendMessage valores : messages) {
			sb.append(NAME_FIELD+DELIMITER + valores.getName()+END_LINE);
			sb.append("Text"+DELIMITER+ valores.getText()+END_LINE);
		}
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
		
	}
	
	public static NCHistoryMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		ArrayList<NCSendMessage> array = new ArrayList<NCSendMessage>();
		for (int i = 1; i < lines.length; i += 2) {
			int idxpuntos1 = lines[i].indexOf(":"); // Posición del delimitador
			int idxpuntos2 = lines[i+1].indexOf(":");
			String fieldName = lines[i].substring(0, idxpuntos1).toLowerCase();
			String valueName = lines[i].substring(idxpuntos1 + 1).trim();
			String fieldText = lines[i+1].substring(0, idxpuntos2).toLowerCase();
			String ValueText = lines[i+1].substring(idxpuntos2 + 1).toLowerCase();
			array.add(new NCSendMessage(NCMessage.OP_SEND_CHAT, valueName, ValueText));
		}
		return new NCHistoryMessage(code, array);
	}

}
