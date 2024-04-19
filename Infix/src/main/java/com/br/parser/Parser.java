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
        this.calculadora();
    }

    private void calculadora(){
        this.calculadoraList();
        this.match(TokenType.EOF);
    }

    private void calculadoraList(){
        Token token = this.lookAhead(1);
        List<TokenType> firsts = List.of(
            TokenType.ABRE_PAR, 
            TokenType.CONST_INT,
            TokenType.CONST_FLOAT,
            TokenType.OP_SUB,
            TokenType.OP_SUM);
        
        if(firsts.contains(token.type())){
            this.calculo();
            this.calculadoraList();
        }
    }

    private void calculo(){
        this.exprArit();
        match(TokenType.PONTO_VIRGULA);
    }

    private void exprArit(){
        this.termo();
        this.exprAritSubRegra();
    }

    private void exprAritSubRegra(){
        Token token = this.lookAhead(1);

        if(token.type() == TokenType.OP_SUM){
            this.match(TokenType.OP_SUM);
            this.exprArit();
        }else if(token.type() == TokenType.OP_SUB){
            this.match(TokenType.OP_SUB);
            this.exprArit();
        }
    }

    private void termo(){
        this.fator();
        this.termoSubRegra();
    }

    private void termoSubRegra(){
        Token token = this.lookAhead(1);

        if(token.type() == TokenType.OP_MUL){
            this.match(TokenType.OP_MUL);
            this.exprArit();
        }else if(token.type() == TokenType.OP_DIV){
            this.match(TokenType.OP_DIV);
            this.exprArit();
        }
    }

    private void fator(){
        this.sinal();

        Token token = this.lookAhead(1);

        if(token.type() == TokenType.CONST_INT){
            this.match(TokenType.CONST_INT);
        }else if(token.type() == TokenType.CONST_FLOAT){
            this.match(TokenType.CONST_FLOAT);
        }else if(token.type() == TokenType.ABRE_PAR){
            this.match(TokenType.ABRE_PAR);
            this.exprArit();
            this.match(TokenType.FECHA_PAR);
        }else{
            throw new RuntimeException("Error");
        }
    }

    private void sinal(){
        Token token = this.lookAhead(1);

        if(token.type() == TokenType.OP_SUM){
            this.match(TokenType.OP_SUM);
        }else if(token.type() == TokenType.OP_SUB){
            this.match(TokenType.OP_SUB);
        }
    }

    @Override
    public void close() throws IOException {
        _lexer.close();
    }

}