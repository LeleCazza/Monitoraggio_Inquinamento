package Progetto.Eccezioni;

public class ErroreIdNodo extends Exception{

    public ErroreIdNodo() {
        super("Attenzione!, Un nodo con quest'id è già presente nella rete!");
    }
}
