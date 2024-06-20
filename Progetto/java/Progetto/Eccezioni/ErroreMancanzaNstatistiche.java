package Progetto.Eccezioni;

public class ErroreMancanzaNstatistiche extends Exception{

    public ErroreMancanzaNstatistiche(){
        super("Errore, le statistiche immagazzinate fin'ora non sono in numero sufficiente");
    }
}
