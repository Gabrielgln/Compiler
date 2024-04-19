package com.br;

import java.io.IOException;

import com.br.parser.Parser;

public class Main {
    public static void main(String[] args) throws IOException{
        //try(var lexer = new Lexer("arquivo.mat")){
        //   Token token;
        //    while ((token = lexer.readNextToken()).type() != TokenType.EOF) {
        //       System.out.print(token);
        //  }
        //}
        Parser parser = new Parser(new Lexer("arquivo.mat"));
        parser.parse();
    }
}