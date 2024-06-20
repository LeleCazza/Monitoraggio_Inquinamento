package Progetto.Eccezioni;

public class ErroreScelta extends Exception{

    public ErroreScelta(){
        super("Errore, la scelta deve necessariamente essere uno dei seguenti numeri: 1, 2, 3, 4");
    }
}
