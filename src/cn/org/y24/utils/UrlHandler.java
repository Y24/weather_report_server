package cn.org.y24.utils;

import cn.org.y24.interfaces.IHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UrlHandler implements IHandler<String, BufferedReader> {
    private BufferedReader reader;

    @Override
    public BufferedReader handle(String target) throws IOException {
        if (reader != null)
            reader.close();
        reader = new BufferedReader(
                new InputStreamReader(
                        new URL(target).openConnection().getInputStream(),
                        StandardCharsets.UTF_8));
        return reader;
    }

    @Override
    public void dispose() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader = null;
        }
    }
}
