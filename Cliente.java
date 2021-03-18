package cliente;

import java.io.*;
import java.net.Socket;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.LocalDateTime;    


public class Cliente extends Thread{
	
	private Socket socket;
	private BufferedReader bufferedr;
	private PrintWriter printw;
	private static int bufferSize;
	private String hash;
	private int id;
	public final static String hola = "HOLA";
	public final static String ok = "OK";
	public final static String error = "ERROR";
	public final static int puerto = 6969;
	public final static String IP = "";

	private InputStream inputs;
	


	public Cliente(int id){
		this.id = id;
		try {
			System.out.println("Se conecta al servidor con IP:  " + IP + " en el puerto:  " + puerto);
			socket = new Socket(IP, puerto);
			System.out.println("Se logra conectar al servidor con IP: " + IP + " en el puerto:  " + puerto);
			inputs = socket.getInputStream();

			bufferSize = socket.getReceiveBufferSize();
			printw = new PrintWriter(socket.getOutputStream(), true);
			bufferedr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Ya puede enviar los archivos");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void run(){

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss");  
		LocalDateTime fecha = LocalDateTime.now(); 
		String titulo = dtf.format(fecha)+"-log";
		File log = new File(RAIZ+"logs/server/"+titulo+".txt");
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		byte[] respuesta;
		String mordiscos;
		try {
			FileWriter filew = new FileWriter(log, true);
			filew.write("Se descargará el archivo video.mp4 y se guardará como test.mp4" + "\n");
			FileOutputStream fileo = new FileOutputStream("./docs/test-"+id+".mp4");
			BufferedOutputStream bufferedos = new BufferedOutputStream(fileo);
			byte[] bytes = new byte[bufferSize];
			int count;
			System.out.println("Empieza la transferencia de los archivos");  // // // // // // // // // // // // // // // // // 
			int n = 0;
			long initio = System.currentTimeMillis();
			while ((count = is.read(bytes)) >= 0) {
				n++;
				bufferedos.write(bytes, 0, count);
				//System.out.println("Descargando el segmento " + n);
			}
			int finito = (int) (System.currentTimeMillis() - initio);
			String tiempo = "";
			if(finito<1000 && finito >99) {
				tiempo = "0." + finito;
			} else {
				finito = finito/1000;
				tiempo += finito;
			}
			File chivaso = new File("./docs/test.mp4");
			filew.write("El tama�o del archivo es: " + chivaso.length() + " bytes" + "\n");
			filew.write("El archivo se descarg� con �xito en un tiempo de " + tiempo + " segundos" + "\n");
			filew.write("Total de segmentos descargados: " + n + " segmentos" + "\n");
			System.out.println("Tiempo total de descarga: " + tiempo + " segundos");
			System.out.println("Finaliza la descarga");
			bufferedos.close();
			inputs.close();
			pw.println("Descarga finalizada. C�digo:1");
			System.out.println(spaghetti);
			System.out.println("Desconectado del servidor");
			filew.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		System.out.println("Ingrese la cantidad de clientes:");
		int numClientes = sc.nextInt();
		Cliente[] clientes = new Cliente[numClientes];
		for(int i = 0; i<clientes.length;i++) {
			clientes[i] = new Cliente(i);
		}
		for (int i = 0; i < clientes.length; i++) {
			clientes[i].run();
			
		}
		
	}
}




