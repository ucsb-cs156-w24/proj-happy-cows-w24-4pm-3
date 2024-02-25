package edu.ucsb.cs156.happiercows.errors;

public class NegativeBuyNumberException extends Exception {
    public NegativeBuyNumberException(String messageString){
        super(messageString);
    }
}
