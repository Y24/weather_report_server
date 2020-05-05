package cn.org.y24;

public class Main {
    private static final int port = 2424;

    public static void main(String[] args) {
        new WebServer().start(port);
    }
}
