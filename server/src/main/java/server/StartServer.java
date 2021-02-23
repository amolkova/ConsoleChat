package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class StartServer {
    private static final int PORT = 8080;
    private static Scanner inputFromConsole;
    private static Scanner inputFromSocket;
    private static Socket socket;
    private static boolean isWork;
    private static final String EXIT = "/end";
    
    public static void main(String[] args) {
        isWork = true;
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("started Server");
            socket = server.accept();
            
            inputFromConsole = new Scanner(System.in, "UTF-8");
            inputFromSocket = new Scanner(socket.getInputStream(), "UTF-8");
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
//чтение с консоли и отправка клиенту
            new Thread(() -> {
                
                while (isWork) {
                    String str = inputFromConsole.nextLine();
                    if (str.equals(EXIT)) {
                        System.out.println("Got command from console. Server disconnected");
                        out.println(EXIT);
                        isWork = false;
                        break;
                    }
                    
                    out.println("Server: " + str);
                    
                }
                
            }).start();
            
//	            	   принятие и вывод на консоль
            
            new Thread(() -> {
                
                while (isWork) {
                    String str = inputFromSocket.nextLine();
                    
                    if (str.equals(EXIT)) {
                        System.out.println("Client disconnected, So server also need to close connection");
                        out.println(EXIT);
                        // comand for closing the first thread from console
                        System.out.println(EXIT);
                        isWork = false;
                        break;
                    }
                    
                    if (str.startsWith("Client")) {
                        System.out.println(str);
                    }
                }
                
            }).start();
            
        } catch (Exception e) {
            e.printStackTrace();
            closeResources();
        }
    }
    
    private static void closeResources() {
        if (inputFromConsole != null) {
            inputFromConsole.close();
        }
        if (inputFromSocket != null) {
            inputFromSocket.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
