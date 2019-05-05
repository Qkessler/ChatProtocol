package es.um.redes.nanoChat.messageFV;


public class NCSendMessage extends NCMessage {

	private String name;
	private String text;
	
	//Campo específico de este tipo de mensaje
	static private final String NAME_FIELD = "name";

	/**
	 * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
	 */
	public NCSendMessage(byte type, String name, String text) {
		this.opcode = type;
		this.name = name;
		this.text = text;
	}

	//Parseamos los campos del mensaje a la codificación correcta en field:value
	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();			
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE); //Construimos el campo
		sb.append(NAME_FIELD+DELIMITER+name+END_LINE); //Construimos el campo
		sb.append("Text"+DELIMITER+text+END_LINE);
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje

	}


	//Parseamos el mensaje contenido en message con el fin de obtener los distintos campos
	public static NCSendMessage readFromString(byte code, String message) {
		String[] lines = message.split(System.getProperty("line.separator"));
		String name = null;
		int idx = lines[1].indexOf(DELIMITER); // Posición del delimitador
		int dospuntos = lines[2].indexOf(DELIMITER);
		String field = lines[1].substring(0, idx).toLowerCase(); 																		// minúsculas
		String value = lines[1].substring(idx + 1).trim();
		String fieldText = lines[2].substring(0, dospuntos);
		String valueMessage = lines[2].substring(dospuntos+1);
		if (field.equalsIgnoreCase(NAME_FIELD))
			name = value;
		return new NCSendMessage(code, name, valueMessage);
	}

	public String getName() {
		return name;
	}
	public String getText() {
		return text;
	}

}
