package es.um.redes.nanoChat.messageFV;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

public class NCInfoMessage extends NCMessage{
	private NCRoomDescription descripcion;
	
	public NCInfoMessage(byte code, NCRoomDescription descripcion) {
		this.opcode = code;
		this.descripcion = descripcion;
	}

	@Override
	public String toEncodedString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public NCRoomDescription getDescripcion() {
		return descripcion;
	}

}
