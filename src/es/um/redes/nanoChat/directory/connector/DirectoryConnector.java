package es.um.redes.nanoChat.directory.connector;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	//Tamaño máximo del paquete UDP (los mensajes intercambiados son muy cortos)
	private static final int PACKET_MAX_SIZE = 128;
	//Puerto en el que atienden los servidores de directorio
	private static final int DEFAULT_PORT = 6868;
	//Valor del TIMEOUT
	private static final int TIMEOUT = 1000;
	
	private static final byte OPCODE_CONSULTA =  1;
	private static final byte OPCODE_RESPONSE_CONSULTA =  2;
	private static final byte OPCODE_CONSULTA_VACIA =  3;
	
	private static final byte OPCODE_REGISTRO =  4;
	private static final byte OPCODE_REGISTRO_OK = 5;
	private static final byte OPCODE_REGISTRO_NO_OK = 6;
	
	
	
	private DatagramSocket socket; // socket UDP
	private InetSocketAddress directoryAddress; // dirección del servidor de directorio

	public DirectoryConnector(String agentAddress) throws IOException {
		//? A partir de la dirección y del puerto generar la dirección de conexión para el Socket
		InetAddress addr = InetAddress.getByName(agentAddress);
		directoryAddress = new InetSocketAddress(addr, DEFAULT_PORT);
		//? Crear el socket UDP
		socket = new DatagramSocket();
		
	}

	/**
	 * Envía una solicitud para obtener el servidor de chat asociado a un determinado protocolo
	 * 
	 */
	public InetSocketAddress getServerForProtocol(int protocol) throws IOException {

		byte[] consulta = buildQuery(protocol);
		//TODO Construir el datagrama con la consulta
		DatagramPacket dp = new DatagramPacket(consulta, consulta.length, directoryAddress);
		//TODO Enviar datagrama por el socket
		socket.send(dp);
		//TODO preparar el buffer para la respuesta
		//TODO Establecer el temporizador para el caso en que no haya respuesta
		socket.setSoTimeout(TIMEOUT);
		//TODO Recibir la respuesta
		byte[] response = new byte[PACKET_MAX_SIZE];
		DatagramPacket packet = new DatagramPacket(response, response.length);
		socket.receive(packet);
		
		ByteBuffer bb = ByteBuffer.wrap(packet.getData());
		Byte opcode = bb.get();
		byte[] IP_array = new byte[4];
		int i = 0;
		while (i<4) {
			byte ip = bb.get(); //Modificar
			IP_array[i] = ip;
			i++;
		}
		int puerto = bb.getInt();
		InetAddress address = InetAddress.getByAddress(IP_array);
		InetSocketAddress directiontosend = new InetSocketAddress(address, puerto); 
		//TODO Procesamos la respuesta para devolver la dirección que hay en ella
		if (opcode == OPCODE_RESPONSE_CONSULTA) {
			return directiontosend;
		}
		else {return null;}
	}

	//Método para generar el mensaje de consulta (para obtener el servidor asociado a un protocolo)
	private byte[] buildQuery(int protocol) {
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put(OPCODE_CONSULTA);
		bb.putInt(protocol);
		
		return bb.array();
	}

	//Método para obtener la dirección de internet a partir del mensaje UDP de respuesta
	private InetSocketAddress getAddressFromResponse(DatagramPacket packet) throws UnknownHostException {
		// Analizar si la respuesta no contiene dirección (devolver null)
		if (packet.getLength() == 1) {
			return null;
		}
		else {
		// Si la respuesta no está vacía, devolver la dirección (extraerla del mensaje)
			InetSocketAddress direction = (InetSocketAddress) packet.getSocketAddress();
			return direction;
		}
	}
	/**
	 * Envía una solicitud para registrar el servidor de chat asociado a un determinado protocolo
	 * 
	 */
	public boolean registerServerForProtocol(int protocol, int port) throws IOException {

		// Construir solicitud de registro (buildRegistration)
		byte[] registration = buildRegistration(protocol, port);
		// Enviar solicitud
		InetSocketAddress serverAddress = new InetSocketAddress(port);
		DatagramPacket pckt = new DatagramPacket(registration, registration.length, serverAddress);
		socket.send(pckt);
		// Recibe respuesta
		socket.receive(pckt);
		byte[] array = pckt.getData();
		ByteBuffer buf = ByteBuffer.wrap(array);
		Byte opcode = buf.get();
		// Procesamos la respuesta para ver si se ha podido registrar correctamente
		if (opcode == OPCODE_REGISTRO_OK) {
			return true;
		}
		else {
			return false;
		}
		
	}


	//Método para construir una solicitud de registro de servidor
	//OJO: No hace falta proporcionar la dirección porque se toma la misma desde la que se envió el mensaje
	private byte[] buildRegistration(int protocol, int port) {
		ByteBuffer bb = ByteBuffer.allocate(9); 
		bb.put(OPCODE_REGISTRO); 
		bb.putInt(protocol); 
		bb.putInt(port);
		byte[] men = bb.array(); 
		//TODO Devolvemos el mensaje codificado en binario según el formato acordado
		return men;
	}

	public void close() {
		socket.close();
	}
}


