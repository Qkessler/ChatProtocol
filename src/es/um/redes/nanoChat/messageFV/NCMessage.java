package es.um.redes.nanoChat.messageFV;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;


public abstract class NCMessage {
	protected byte opcode;

	//TODO Implementar el resto de los opcodes para los distintos mensajes
	public static final byte OP_INVALID_CODE = 0;
	public static final byte OP_NICK = 1;
	public static final byte OP_GET_ROOMLIST = 2;
	public static final byte OP_SEND_ROOMLIST = 7;
	public static final byte OP_ENTER_ROOM = 3;
	public static final byte OP_LEAVE_ROOM = 4;
	public static final byte OP_REMOVE_USER = 5;
	public static final byte OP_GET_ROOMINFO = 6;
	public static final byte OP_NICK_OK = 8;
	public static final byte OP_NICK_DUPLICADO = 9;
	public static final byte OP_ENTER_TRUE = 10;
	public static final byte OP_ENTER_FALSE = 11;
	public static final byte OP_SEND_ROOMINFO = 12;
	public static final byte OP_EXIT = 13;
	public static final byte OP_SEND_CHAT = 14;
	public static final byte OP_GET_HISTORY = 15;
	
	//Constantes con los delimitadores de los mensajes de field:value
	public static final char DELIMITER = ':';    //Define el delimitador
	public static final char END_LINE = '\n';    //Define el carácter de fin de línea
	
	public static final String OPCODE_FIELD = "operation";

	

	/**
	 * Códigos de los opcodes válidos  El orden
	 * es importante para relacionarlos con la cadena
	 * que aparece en los mensajes
	 */
	private static final Byte[] _valid_opcodes = { 
		OP_NICK, OP_NICK_OK, OP_NICK_DUPLICADO, OP_GET_ROOMLIST, 
		OP_SEND_ROOMLIST, OP_ENTER_ROOM, OP_ENTER_TRUE, OP_ENTER_FALSE, 
		OP_LEAVE_ROOM, OP_REMOVE_USER, OP_GET_ROOMINFO, OP_SEND_ROOMINFO,
		OP_EXIT, OP_SEND_CHAT, OP_GET_HISTORY
		};

	/**
	 * cadena exacta de cada orden
	 */
	private static final String[] _valid_operations = {
		"Nick", "Nick_OK", "Nick_DUPLICATED", "getRoomList", "sendRoomList",
		"enterRoom", "enter_True", "enter_False", "leaveRoom", "removeUser",
		"getRoomInfo", "sendRoomInfo", "exit", "sendChat", "getHistory"
		};

	/**
	 * Transforma una cadena en el opcode correspondiente
	 */
	protected static byte operationToOpcode(String opStr) {
		//Busca entre los opcodes si es válido y devuelve su código
		for (int i = 0;	i < _valid_operations.length; i++) {
			if (_valid_operations[i].equalsIgnoreCase(opStr)) {
				return _valid_opcodes[i];
			}
		}
		//Si no se corresponde con ninguna cadena entonces devuelve el código de código no válido
		return OP_INVALID_CODE;
	}

	/**
	 * Transforma un opcode en la cadena correspondiente
	 */
	protected static String opcodeToOperation(byte opcode) {
		//Busca entre los opcodes si es válido y devuelve su cadena
		for (int i = 0;	i < _valid_opcodes.length; i++) {
			if (_valid_opcodes[i] == opcode) {
				return _valid_operations[i];
			}
		}
		//Si no se corresponde con ningún opcode entonces devuelve null
		return null;
	}
	
	
	
	//Devuelve el opcode del mensaje
	public byte getOpcode() {
		return opcode;

	}

	//Método que debe ser implementado específicamente por cada subclase de NCMessage
	protected abstract String toEncodedString();

	//Extrae la operación del mensaje entrante y usa la subclase para parsear el resto del mensaje
	public static NCMessage readMessageFromSocket(DataInputStream dis) throws IOException {
		String message = dis.readUTF();
		String[] lines = message.split(System.getProperty("line.separator"));
		if (!lines[0].equals("")) { // Si la línea no está vacía
			int idx = lines[0].indexOf(DELIMITER); // Posición del delimitador
			String field = lines[0].substring(0, idx).toLowerCase(); 			
			String value = lines[0].substring(idx + 1).trim();
			if (!field.equalsIgnoreCase(OPCODE_FIELD)) return null;
			byte code = operationToOpcode(value);
			if (code == OP_INVALID_CODE) return null;
			switch (code) {
			case OP_NICK:
			{
				return NCRoomMessage.readFromString(code, message);
			}
			case OP_NICK_OK:
			{
				return NCOpcodeMessage.readFromString(code);
			}
			case OP_NICK_DUPLICADO:
			{
				return NCOpcodeMessage.readFromString(code);
			}
			case OP_GET_ROOMLIST:
			{
				return new NCOpcodeMessage(code);
			}
			case OP_SEND_ROOMLIST:
			{
				return NCRoomListMessage.readFromString(code, message);
			}
			case OP_ENTER_ROOM:
			{
				return NCRoomMessage.readFromString(code, message);
			}
			case OP_ENTER_TRUE:
			{
				return NCOpcodeMessage.readFromString(code);
			}
			case OP_ENTER_FALSE:
			{
				return NCOpcodeMessage.readFromString(code);
			}
			case OP_GET_ROOMINFO:
			{
				return NCOpcodeMessage.readFromString(code);
			}
			case OP_SEND_ROOMINFO:
			{
				return NCInfoMessage.readFromString(code, message);
			}
			case OP_EXIT:
			{
				return NCRoomMessage.readFromString(code, message);
			}
			case OP_SEND_CHAT:
			{
				return NCSendMessage.readFromString(code, message);
			}
			case OP_GET_HISTORY:
				return NCHistoryMessage.readFromString(code, message);
			default:
				System.err.println("Unknown message type received:" + code);
				return null;
			}
		} else
			return null;
	}

	public static NCMessage makeOpcodeMessage(byte code) {
		return (new NCOpcodeMessage(code));
	}
	//Método para construir un mensaje de tipo Room a partir del opcode y del nombre
	public static NCMessage makeRoomMessage(byte code, String name) {
		return (new NCRoomMessage(code, name));
	}
	
	public static NCMessage makeRoomListMessage(byte opcode, ArrayList<NCRoomDescription> roomList) {
		return (new NCRoomListMessage(opcode, roomList));
	}
	
	public static NCMessage makeInfoMessage(byte code, NCRoomDescription descripcion) {
		return (new NCInfoMessage(code, descripcion));
	}

	public static NCSendMessage makeSendMessage(byte opSendChat, String name, String text) {		//, long date
		return (new NCSendMessage(opSendChat, name, text));		//, date
	}

	public static NCHistoryMessage makeHistoryMessage(byte code, ArrayList<NCSendMessage> messages) {
		return (new NCHistoryMessage(code, messages));
	}

}
