import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private static ServerSocket socket;

    public Servidor (int puerto){
        try {
			socket = new ServerSocket(puerto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
		Boolean ciclo = false;
		while(!ciclo) {
            System.out.println("Seleccione la opción que desea realizar");
            System.out.println("1. Enviar archivos");
            String opcion = scan.next();
            switch(opcion) {
                case "1": {
                    System.out.println("Seleccione el archivo a enviar entre las siguientes opciones:");
                    System.out.println("1. Archivo1");
                    System.out.println("2. Archivo2");
                    String seleccionado = scan.next();
                    System.out.println("Ingrese el puerto a utilizar");
                    int puerto = scan.nextInt();
                    System.out.println("Ingrese el numero de clientes a enviar el archivo");
                    int nClientes = scan.nextInt();
                    Servidor server = new Servidor(puerto);
                    if(seleccionado.equals("1")){
                        server.envioArchivos(puerto, nClientes, "archivo1");
                        ciclo=true;
                    }
                    else if (seleccionado.equals("2")) {
                        server.envioArchivos(puerto, nClientes, "archivo2");
                        ciclo=true;
                    }
                    break;

                }
                default:
                    System.out.println("Ingrese una opción correcta");
            }
        }


    }


    public void envioArchivos(int puerto, int nClientes, String archivo){
        ExecutorService executor = Executors.newFixedThreadPool(nClientes);

        for(int i=0; i<nClientes; i++){

            try{
                Socket sock = socket.accept();

                System.out.println("Conexión con cliente " + i + " iniciada");
                Hilo hilo = new Hilo(i, archivo, sock);
                executor.execute(hilo);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public class Hilo extends Thread{

        private String archivo;

        private int id;
        
        private Socket sock=null;

        public Hilo(int id, String archivo, Socket sock){
            this.id=id;
            this.archivo=archivo;
            this.sock=sock;
        }

        public void run(){


            FileInputStream fis;
			BufferedInputStream bis;
			BufferedOutputStream bos;
			byte[] buffer = new byte[8192];

            try{
                File envio = new File("ROOT"+"docs/"+archivo);
				fis = new FileInputStream("ROOT"+"docs/"+archivo);
				bis = new BufferedInputStream(fis);
				bos = new BufferedOutputStream(sock.getOutputStream());
                int x;

                int n = 0;
                while ((x = bis.read(buffer)) > 0) {
					n++;
					bos.write(buffer, 0, x);
				}

                bos.close();
				fis.close();
				bis.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        
    }


}



