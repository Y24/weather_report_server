package cn.org.y24;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WebServer {
    private static final String dbUrlPrefix = "jdbc:mysql://localhost:3306/";
    private static final String dbName = "weather_report";
    private static final String option = "?useUnicode=true&characterEncoding=utf-8";
    private static final String username = "y24";
    private static final String password = "yue";

    public void start(int port) {
        // Connection number
        int i = 0;
        try {
            Connection rootConnection = DriverManager.getConnection(dbUrlPrefix + dbName + option, username, password);
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("The connection number is: " + i++);
                new Thread(new HandlerThread(rootConnection, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error occurs when get connection to the database.");
        }
    }
}
