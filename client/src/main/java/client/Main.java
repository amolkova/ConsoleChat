package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 8080;
    private static Scanner inputFromConsole;
    private static Scanner inputFromSocket;
    private static Socket socket;
    private static boolean isWork;
    private static final String EXIT = "/end";
    
    public static void main(String[] args) {
        isWork = true;
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            System.out.println("started Client");
            
            inputFromConsole = new Scanner(System.in, StandardCharsets.UTF_8);
            inputFromSocket = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
// чтение с консоли и отправка серверу
            new Thread(() -> {
                
                while (isWork) {
                    if (inputFromConsole.hasNextLine()) {
                        String str = inputFromConsole.nextLine();
                        
                        if (str.equals(EXIT)) {
                            System.out.println("Got command from console. Client disconnected");
                            out.println(EXIT);
                            isWork = false;
                            break;
                        }
                        out.println("Client: " + str);
                        
                    }
                }
            }).start();
            
//	            	   принятие и вывод на консоль
            
            new Thread(() -> {
                
                while (isWork) {
                    String str = inputFromSocket.nextLine();
                    if (str.equals(EXIT)) {
                        System.out.println("Server disconnected");
                        out.println(EXIT);
                        // comand for closing the first thread from console
                        System.out.println(EXIT);
                        isWork = false;
                        break;
                    }
                    
                    if (str.startsWith("Server")) {
                        System.out.println(str);
                    }
                }
                
            }).start();
            
        } catch (Exception e1) {
            e1.printStackTrace();
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
