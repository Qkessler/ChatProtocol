package es.um.redes.nanoChat.directory.server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class DirectoryThread extends Thread {

	//Tamaño máximo del paquete UDP
	private static final int PACKET_MAX_SIZE = 128;
	//Estructura para guardar las asociaciones ID_PROTOCOLO -> Dirección del servidor
	protected HashMap<Integer,InetSocketAddress> servers;

	//Socket de comunicación UDP
	protected DatagramSocket socket;
	//Probabilidad de descarte del mensaje
	protected double messageDiscardProbability;
	
	private static final byte OPCODE_CONSULTA =  1;
	private static final byte OPCODE_RESPONSE_CONSULTA =  2;
	private static final byte OPCODE_CONSULTA_VACIA =  3;
	
	private static final byte OPCODE_REGISTRO =  4;
	private static final byte OPCODE_REGISTRO_OK = 5;
	private static final byte OPCODE_REGISTRO_NO_OK = 6;
	

	public DirectoryThread(String name, int directoryPort,
			double corruptionProbability)
			throws SocketException {
		super(name);
		// Anotar la dirección en la que escucha el servidor de Directorio
 		// Crear un socket de servidor
		InetSocketAddress serverAddress = new InetSocketAddress(directoryPort);
		socket = new DatagramSocket(serverAddress);
		messageDiscardProbability = corruptionProbability;
		//Inicialización del mapa
		 servers = new HashMap<Integer,InetSocketAddress>();
	}

	public void run() {
		byte[] buf = new byte[PACKET_MAX_SIZE];

		System.out.println("Directory starting...");
		boolean running = true;
		while (running) {
				
			try {
				// 1) Recibir la solicitud por el socket 
				DatagramPacket pckt = new DatagramPacket(buf, buf.length);
				socket.receive(pckt);
				// 2) Extraer quién es el cliente (su dirección) 
				InetSocketAddress client = (InetSocketAddress)pckt.getSocketAddress();
				// 3) Vemos si el mensaje debe ser descartado por la probabilidad de descarte 

				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println("Directory DISCARDED corrupt request from... ");
					continue;
				}
				
				//TODO 4) Analizar y procesar la solicitud (llamada a processRequestFromCLient)
				//processRequestFromClient(, client);
				processRequestFromClient(pckt.getData(), client);
				
				//TODO 5) Tratar las excepciones que puedan producirse
				} catch(IOException e) {
					//Tratar con lo que nos interese.
				}
			}
				

		socket.close();
	}

	//Método para procesar la solicitud enviada por clientAddr
	public void processRequestFromClient(byte[] data, InetSocketAddress clientAddr) throws IOException {
		// 1) Extraemos el tipo de mensaje recibido
		ByteBuffer bb = ByteBuffer.wrap(data);
		Byte opcode = bb.get();
		byte[] IP_array = new byte[4];
		int i = 0;
		while (i<4) {
			byte ip = bb.get(); 
			IP_array[i] = ip;
			i++;
		}
		int puerto = bb.getInt();
		InetAddress address = InetAddress.getByAddress(IP_array);
		InetSocketAddress serverAddress = new InetSocketAddress(address, puerto); 
		int protocol = bb.getInt();
		switch(opcode) {
		// 3) Procesar el caso de que sea una consulta
			case OPCODE_CONSULTA:	
		// 3.1) Devolver una dirección si existe un servidor (sendServerInfo)
				if (servers.containsKey(protocol)) {
					sendServerInfo(serverAddress, clientAddr, protocol);
				}
		// 3.2) Devolver una notificación si no existe un servidor (sendEmpty)
				else {sendEmpty(clientAddr);}
		// 2) Procesar el caso de que sea un registro y enviar mediante sendOK
			case OPCODE_REGISTRO:
				if (!servers.containsKey(protocol)) {
					servers.put(protocol, serverAddress);
					sendOK(clientAddr);
				}
				else {
					servers.put(protocol, serverAddress);
					sendNOOK(clientAddr);
				}
		}
	}

	//Método para enviar una respuesta vacía (no hay servidor)
	private void sendEmpty(InetSocketAddress clientAddr) throws IOException {
		//TODO Construir respuesta
		byte[] buf = new byte[1];
		buf[0] = OPCODE_CONSULTA_VACIA;
		DatagramPacket dp = new DatagramPacket(buf, buf.length, clientAddr);
		//TODO Enviar respuesta
		socket.send(dp);
		
	}

	//Método para enviar la dirección del servidor al cliente
	private void sendServerInfo(InetSocketAddress serverAddress, InetSocketAddress clientAddr, int protocol) throws IOException {
		//TODO Obtener la representación binaria de la dirección
		//TODO Construir respuesta
		int puerto = serverAddress.getPort();
		ByteBuffer bf = ByteBuffer.allocate(13);
		bf.put(OPCODE_RESPONSE_CONSULTA);
		bf.put(serverAddress.getAddress().getAddress()); 	   //IP
		bf.putInt(puerto); //Puerto
		bf.putInt(protocol);//Protocolo
		byte[] array = bf.array();
		DatagramPacket dp = new DatagramPacket(array, array.length, clientAddr);
		//TODO Enviar respuesta
		socket.send(dp);
	}

	//Método para enviar la confirmación del registro
	private void sendOK(InetSocketAddress clientAddr) throws IOException {
		//TODO Construir respuesta
		byte[] buf = new byte[1];
		buf[0] = OPCODE_REGISTRO_OK;
		DatagramPacket dp =  new DatagramPacket(buf, buf.length);
		//TODO Enviar respuesta

	}
	
	private void sendNOOK(InetSocketAddress clientAddr) throws IOException {
		//TODO Construir respuesta
		byte[] buf = new byte[1];
		buf[0] = OPCODE_REGISTRO_NO_OK;
		DatagramPacket dp =  new DatagramPacket(buf, buf.length);
		//TODO Enviar respuesta
		socket.send(dp);
	}	
}
