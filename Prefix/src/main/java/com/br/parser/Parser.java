package com.br.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.br.Lexer;
import com.br.parser.exceptions.SyntaxError;
import com.br.Token;
import com.br.TokenType;

public class Parser implements Closeable{
    
    private static final int BUFFER_SIZE = 10;
    private List<Token> _tokens;
    private Lexer _lexer;

    public Parser(Lexer lexer) throws IOException {
        this._lexer = lexer;
        _tokens = new ArrayList<>();
        confirmToken();
    }

    private void confirmToken() throws IOException {
    
        if(_tokens.size() > 0) _tokens.remove(0);
        
        while(_tokens.size() < BUFFER_SIZE){
            var next = _lexer.readNextToken();

            if(next.type() == TokenType.COMMENT) 
                continue;
                
            _tokens.add(next);

            if(next.type() == TokenType.EOF) 
                break;
        }
    }

    private Token lookAhead(int k) {
        if(_tokens.isEmpty()) return null;

        return k-1 >= _tokens.size() ? _tokens.get(_tokens.size()-1) : _tokens.get(k-1);
    }

    private void printMatchToken(Token token){
        System.out.println("Match: " + token);
        if(token.type() == TokenType.PONTO_VIRGULA)
            System.out.println("");
    }

    private void match(TokenType type){
        var la = lookAhead(1);

        if(la.type() == type){
            printMatchToken(la);
            try{
                confirmToken();
            }catch(IOException ex){
                System.out.println(ex.getLocalizedMessage());
            }
        }else{
            throw new SyntaxError(la, type);
        }
    }

    public void parse(){
        this.gramaticaPrefixa();
    }

    private void gramaticaPrefixa(){
        Token token = this.lookAhead(1);

        if(token.type() == TokenType.EOF){
            match(TokenType.EOF);
        }else{
            this.expressao();
            match(TokenType.PONTO_VIRGULA);
            this.gramaticaPrefixa();
        }
    }

    private void expressao(){
        Token token = this.lookAhead(1);
        List<TokenType> firsts = List.of(
            TokenType.OP_DIV,
            TokenType.OP_MUL,
            TokenType.OP_SUB,
            TokenType.OP_SUM);
        if(firsts.contains(token.type())){
            this.operacao();
        }else{
            this.numero();
        }
    }

    private void operacao(){
        this.opArit();
        this.expressao();
        this.expressao();
    }

    private void opArit(){
        Token token = this.lookAhead(1);

        switch (token.type()) {
            case OP_SUM:
                match(TokenType.OP_SUM);
                break;
            case OP_SUB:
                match(TokenType.OP_SUB);
                break;
            case OP_MUL:
                match(TokenType.OP_MUL);
                break;
            case OP_DIV:
                match(TokenType.OP_DIV);
                break;
        }
    }

    private void numero(){
        Token token = this.lookAhead(1);

        if(token.type() == TokenType.CONST_INT){
            match(TokenType.CONST_INT);
        }else if(token.type() == TokenType.CONST_FLOAT){
            match(TokenType.CONST_FLOAT);
        }
    }

    @Override
    public void close() throws IOException {
        _lexer.close();
    }
}