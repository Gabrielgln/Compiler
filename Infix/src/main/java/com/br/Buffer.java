package com.br;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Buffer implements Closeable {

    private PushbackReader _buffer;
    public static final int EOF = -1;

    public Buffer(String programPath) throws FileNotFoundException{
        _buffer = new PushbackReader(new FileReader(programPath));
    }

    public Buffer(){}

    public int readNextChar() throws IOException{
        return _buffer.read();
    }

    public void pushback(int symbol) throws IOException{
        _buffer.unread(symbol);
    }

    @Override
    public void close() throws IOException{
        if(_buffer != null) 
            _buffer.close();
    }
}
