package es.um.redes.nanoChat.messageFV;

public class NCOpcodeMessage extends NCMessage {
	
	public NCOpcodeMessage (byte opcode) {
		this.opcode = opcode;
	}
	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();			
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE); //Construimos el campo
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
	}
	
	public static NCOpcodeMessage readFromString(byte code) {
		return new NCOpcodeMessage(code);
	}

}
