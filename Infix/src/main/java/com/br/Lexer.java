package com.br;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Lexer implements Closeable{
    
    private Buffer _buffer;
    private List<Character> _operators = List.of('+', '-', '*', '/');
    private List<Character> _charSpecial = List.of('(', ')', ';');

    public Lexer(String programPath) throws FileNotFoundException{
        _buffer = new Buffer(programPath);
    }

    public Token readNextToken() throws IOException{
        int charCurrent;
        while((charCurrent = _buffer.readNextChar()) != _buffer.EOF){
            var charConverted = (char) charCurrent;
            if(Character.isWhitespace(charConverted)){
                continue;
            }else if(_operators.contains(charConverted)){
                return readOperator(charConverted);
            }else if(_charSpecial.contains(charConverted)){
                return readCharSpecial(charConverted);
            }else if(Character.isDigit(charConverted)){
                return readNumber(charConverted);
            }else if(charConverted == '#'){
                return readComment(charConverted);
            }
        }
        return new Token(TokenType.EOF, null);
    }

    private Token readOperator(char initialSymbol) {
        switch (initialSymbol) {
            case '+':
                return new Token(TokenType.OP_SUM, "+");
            case '-':
                return new Token(TokenType.OP_SUB, "-");
            case '*':
                return new Token(TokenType.OP_MUL, "*");
            case '/':
                return new Token(TokenType.OP_DIV, "/");
            default:
                throw new RuntimeException("Lexema não reconhecido");
        }
    }

    private Token readCharSpecial(char initialSymbol) {
        switch (initialSymbol) {
            case '(':
                return new Token(TokenType.ABRE_PAR, "(");
            case ')':
                return new Token(TokenType.FECHA_PAR, ")");
            case ';':
                return new Token(TokenType.PONTO_VIRGULA, ";");
            default:
                throw new RuntimeException("Lexema não reconhecido");
        }
    }

    private Token readNumber(char initialSymbol) throws IOException {
        var lexema = new StringBuilder();
        lexema.append(initialSymbol);

        boolean hasPlaceDecimal = false;
        boolean lastWasNumber = true; //Tratamento caso o número seja nesse formato: 3.

        int charCurrent;
        while((charCurrent = _buffer.readNextChar()) != _buffer.EOF){
            char charConverted = (char) charCurrent;
            if(Character.isDigit(charConverted)){
                lexema.append(charConverted);
                lastWasNumber = true;
            }else if(!hasPlaceDecimal && charConverted == '.'){
                lexema.append(charConverted);
                hasPlaceDecimal = true;
                lastWasNumber = false;
            }else{
                _buffer.pushback(charCurrent);
                break;
            }
        }
        if(!lastWasNumber) throw new RuntimeException("Lexema não reconhecido");

        return new Token(hasPlaceDecimal ? TokenType.CONST_FLOAT : TokenType.CONST_INT, lexema.toString());
    }

    private Token readComment(char initialSymbol) throws IOException {
        var lexema = new StringBuilder();
        int charCurrent;
        while((charCurrent = _buffer.readNextChar()) != _buffer.EOF){
            char charConverted = (char) charCurrent;
            if(charConverted == '\n')
                break;
            lexema.append(charConverted);
        }
        return new Token(TokenType.COMMENT, lexema.toString());
    }

    @Override
    public void close() throws IOException {
        if(_buffer != null)
            _buffer.close();
    }
}
