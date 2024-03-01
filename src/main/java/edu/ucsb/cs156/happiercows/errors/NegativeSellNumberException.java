package edu.ucsb.cs156.happiercows.errors;

public class NegativeSellNumberException extends Exception {
    public NegativeSellNumberException(String messageString){
        super(messageString);
    }
}
