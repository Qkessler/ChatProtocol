package es.um.redes.nanoChat.directory.server;
import java.io.IOException;
import java.net.InetSocketAddress;

import es.um.redes.nanoChat.directory.connector.DirectoryConnector;

public class Tester {

	public static void main(String[] args) throws IOException {
		DirectoryConnector dc = new DirectoryConnector("localhost");
		Boolean registro = dc.registerServerForProtocol(0, 6969);
		if (registro == true){System.out.println("Registro ha sido guardado correctamente");}
		InetSocketAddress address = dc.getServerForProtocol(0);
		
		if (address == null) {System.out.println("Servidor no encontrado");}
		else System.out.println("El servidor es:" + address);
		
		

	}

}
