package cn.org.y24.interfaces;

import java.io.IOException;

public interface IHandler<T, V> {
    V handle(T target) throws IOException;
    void dispose();
}
