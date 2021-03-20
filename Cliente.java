import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.time.LocalDateTime;    


public class Cliente extends Thread{
	
	private Socket socket;
	private BufferedInputStream bufferedr;
	private BufferedOutputStream printw;
	private int id;
	public final static String hola = "HOLA";
	public final static String ok = "OK";
	public final static String error = "ERROR";
	public final static int puerto = 6969;
	public final static String IP = "";
    private final static String RAIZ = "./../../";

	private InputStream inputs;
	
	public Cliente(int id){
		this.id = id;
		try {
			System.out.println("Se conecta al servidor con IP:  " + IP + " en el puerto:  " + puerto);
			socket = new Socket(IP, puerto);
			System.out.println("Se logra conectar al servidor con IP: " + IP + " en el puerto:  " + puerto);
			inputs = socket.getInputStream();

			printw = new BufferedOutputStream(socket.getOutputStream());
			bufferedr = new BufferedInputStream(socket.getInputStream());
			System.out.println("Ya puede enviar los archivos");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void run(){

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss");  
		LocalDateTime fecha = LocalDateTime.now(); 
		String titulo = dtf.format(fecha)+"-log";
		File log = new File(RAIZ+"/logs/client/"+titulo+".txt");
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1"); 
			FileWriter filew = new FileWriter(log, true);
			filew.write("Se descargará el archivo video.txt y se guardará como test.txt" + "\n");
			FileOutputStream fileo = new FileOutputStream("./ArchivosRecibidos/test-"+id+".txt");
			BufferedOutputStream bufferedos = new BufferedOutputStream(fileo);
			byte[] bytes = new byte[8192];
			int count;
			System.out.println("Empieza la transferencia de los archivos");
			int n = 0;
			long initio = System.currentTimeMillis();
			while ((count = bufferedr.read(bytes)) >= 0) {
				n++;
				bufferedos.write(bytes, 0, count);
				//System.out.println("Descargando el segmento " + n);
			}
			printw.write("Fin".getBytes("UTF-8"));
			int finito = (int) (System.currentTimeMillis() - initio);
			String tiempo = "";
			byte[] hashL = md.digest(Files.readAllBytes(Paths.get("./ArchivosRecibidos/test-"+id+".txt")));
			byte[] hash = new byte[hashL.length];
			bufferedr.read(hash);
			boolean b = Arrays.equals(hashL, hash);

			if(finito<1000 && finito >99) {
				tiempo = "0." + finito;
			} else {
				finito = finito/1000;
				tiempo += finito;
			}
			File chivaso = new File("./ArchivosRecibidos/test.txt");
			filew.write("El tama�o del archivo es: " + chivaso.length() + " bytes" + "\n");
			filew.write("El archivo se descarg� con �xito en un tiempo de " + tiempo + " segundos" + "\n");
			filew.write("El archivo se descargo correctamente? " + b + "\n");
			filew.write("Total de segmentos descargados: " + n + " segmentos" + "\n");
			System.out.println("Tiempo total de descarga: " + tiempo + " segundos");
			System.out.println("Finaliza la descarga");
			bufferedos.close();
			inputs.close();
			System.out.println("Desconectado del servidor");
			filew.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
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
		sc.close();
	}
}




