import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Servidor {

    private static ServerSocket socket;

    private final static String RAIZ = "./";

    public Servidor(int puerto) {
        try {
            socket = new ServerSocket(puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Boolean ciclo = false;
        while (!ciclo) {
            System.out.println("Seleccione la opción que desea realizar");
            System.out.println("1. Enviar archivos");
            String opcion = scan.next();
            if (opcion.equals("1")) {

                System.out.println("Seleccione el archivo a enviar entre las siguientes opciones:");
                System.out.println("1. Archivo1");
                System.out.println("2. Archivo2");
                String seleccionado = scan.next();
                System.out.println("Ingrese el puerto a utilizar");
                int puerto = scan.nextInt();
                System.out.println("Ingrese el numero de clientes a enviar el archivo");
                int nClientes = scan.nextInt();
                Servidor server = new Servidor(puerto);
                if (seleccionado.equals("1")) {
                    server.envioArchivos(puerto, nClientes, "archivo1");
                    ciclo = true;
                } else if (seleccionado.equals("2")) {
                    server.envioArchivos(puerto, nClientes, "archivo2");
                    ciclo = true;
                }
            }
        }
        scan.close();

    }

    public void envioArchivos(int puerto, int nClientes, String archivo) {

        Hilo[] hilos = new Hilo[nClientes];

        ExecutorService executor = Executors.newFixedThreadPool(nClientes);

        for (int i = 0; i < nClientes; i++) {

            try {
                Socket sock = socket.accept();

                System.out.println("Conexión con cliente " + i + " iniciada");
                Hilo hilo = new Hilo(i, archivo, sock);
                executor.execute(hilo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Hilo extends Thread {

        private String archivo;

        private int id;

        private Socket sock = null;

        public Hilo(int id, String archivo, Socket sock) {
            this.id = id;
            this.archivo = archivo;
            this.sock = sock;
        }

        public void run() {

            FileInputStream fis;
            BufferedInputStream bis;
            BufferedOutputStream bos;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss");
            LocalDateTime fecha = LocalDateTime.now();
            String titulo = dtf.format(fecha) + "-log";
            File log = new File(RAIZ + "logs/server/" + titulo + ".txt");
            dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            try {
                byte[] hash = calculateMD5Digest(
                        new BufferedInputStream(new FileInputStream(RAIZ + "data/" + archivo + ".txt")));
                byte[] buffer = new byte[sock.getSendBufferSize()];
                BufferedInputStream bufferedr = new BufferedInputStream(sock.getInputStream());
                File envio = new File(RAIZ + "data/" + archivo + ".txt");
                byte[] arreglobytes = new byte[(int) envio.length()];
                fis = new FileInputStream(envio);
                bis = new BufferedInputStream(fis);
                bos = new BufferedOutputStream(sock.getOutputStream());
                System.out.println("Envio iniciado");

                FileWriter fw = new FileWriter(log, true);
                fw.write("El archivo a enviar es " + archivo + " con un peso de " + envio.length() + " bytes" + "\n");
                fw.write("El cliente al que se envia este archivo es el cliente " + id);
                // bis.read(arreglobytes, 0,arreglobytes.length);
                MessageDigest md = MessageDigest.getInstance("MD5");
                long tInicial = System.currentTimeMillis();
                int count = 0;
                bos.write(hash);
                Thread.sleep(2000);
                while (bufferedr.available() > 0) {
                    bufferedr.read(arreglobytes);
                }
                Thread.sleep(2000);
                while ((count = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, count);
                }

                int tFinal = (int) (System.currentTimeMillis() - tInicial);
                fw.write("El archivo se ha enviado exitosamente en un tiempo de " + tFinal + " milisegundos");
                fw.write("Total de bytes enviados:  " + envio.length());

                bos.flush();
                bos.close();
                fis.close();
                bis.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        public byte[] calculateMD5Digest(InputStream is) throws NoSuchAlgorithmException, IOException {
            int bytesRead = 0;
            byte[] buffer = new byte[2048];
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((bytesRead = is.read(buffer)) != -1) {
                md5.update(buffer, 0, bytesRead);
            }
            return md5.digest();
        }

    }

}
