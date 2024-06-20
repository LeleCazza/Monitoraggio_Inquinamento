package Progetto.Nodo;

import Progetto.Eccezioni.ErroreIdNodo;
import Progetto.Messaggi;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@XmlRootElement
public class Nodo implements Comparable<Nodo>{
    @XmlTransient
    private ComunicatoreConGateway comunicatoreConGateway;
    @XmlTransient
    private ComunicatoreTraNodi comunicatoreTraNodi;
    @XmlTransient
    private Sensore sensore;
    @XmlTransient
    public final Object lockMediaLocale = "LOCK", lockNuovaMedia = "LOCK",
            lockToken = "TOKEN", lockEsciDallaRete = "LOCK", locklistaNodi = "LOCK";
    @XmlTransient
    public boolean nuovaMedia = false, token = false, esciDallaRete=false;
    @XmlTransient
    public double mediaLocale = 0;
    @XmlTransient
    public Messaggi.Token messaggioToken = null;
    @XmlTransient
    public ArrayList<Nodo> listaNodi;
    @XmlTransient
    public ArrayList<Nodo> listaNodiEliminati;

    private int idnodo, portanodo;

    public Nodo(){}
    public Nodo(int idnodo, int portanodo) {
        this.idnodo = idnodo;
        this.portanodo = portanodo;
    }

    public int getIdnodo() {
        return idnodo;
    }
    public void setIdnodo(int idnodo) { this.idnodo = idnodo; }
    public int getPortanodo() {
        return portanodo;
    }
    public void setPortanodo(int portanodo) { this.portanodo = portanodo; }

    @Override
    public int compareTo(Nodo o) {
        return Integer.compare(this.idnodo, o.idnodo);
    }

    public void ImpostaIdDaConsole(){
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        int idNodo = 0;
        boolean controlloInserimento = true;
        while(controlloInserimento){
            System.out.println("Inserisci l'Id del Nodo: ");
            try {
                idNodo = Integer.parseInt(read.readLine());
                if(idNodo<=0)
                    throw new java.lang.NumberFormatException();
                controlloInserimento = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.lang.NumberFormatException e){
                System.out.println("L'identificatore deve essere necessariamente un numero positivo");
            }
        }
        this.idnodo = idNodo;
    }

    public void EntraNellaRete() throws ErroreIdNodo {
        InizializzaComponenti();
        comunicatoreConGateway.AddNodoToGateway();
        listaNodi = comunicatoreConGateway.GetListaNodiFromGateway();
        listaNodiEliminati = new ArrayList<>();
    }

    private void InizializzaComponenti(){
        comunicatoreConGateway = new ComunicatoreConGateway(this);
        comunicatoreTraNodi = new ComunicatoreTraNodi(this);
        sensore = new Sensore(this);
        new UscitaControllata(this).start();
    }

    public void AvviaSensore(){
        sensore.start();
    }

    public boolean SonoDaSoloNellaRete(){
        return listaNodi.size() == 1;
    }

    public void AvviaServerPerComunicazioneTraNodi(){
        comunicatoreTraNodi.AvviaServiziGrpc();
    }

    public void AttendiNuovaMediaLocale(){
        synchronized (lockNuovaMedia) {
            while(true){
                if (nuovaMedia){
                    nuovaMedia = false;
                    System.out.println("Ora ho una MediaLocale!");
                    break;
                }
                else{
                    System.out.println("Non ho ancora una MediaLocale, aspetto...");
                    try {
                        lockNuovaMedia.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void InviaMediaLocaleAlGateway(){
        System.out.println("Invio la mia media LOCALE " + mediaLocale + " al Gateway");
        comunicatoreConGateway.InviaNuovaMediaAlGateway(mediaLocale);
    }

    public void SincronizzaListaNodiConVicini(){
        comunicatoreTraNodi.SincronizzaListaNodi();
    }

    public void PassaTokenAlSuccessivo(){ comunicatoreTraNodi.PassaTokenAlSuccessivo(); }

    public void AttendiToken(){
        synchronized (lockToken){
            while(!token){
                try {
                    lockToken.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Ho ricevuto il Token!");
        }
    }

    public boolean TokenConMedieCompleto(){
        if(messaggioToken != null){
            for (Messaggi.Token.Media media: messaggioToken.getMediaList()){
                if(media.getIdNodo() == idnodo){
                    System.out.println("Il token è completo!");
                    return true;
                }
            }
        }
        return false;
    }

    public void InviaMediaDistribuitaAlGateway(){
        double media = CalcolaMediaDistribuita();
        System.out.println("Invio la media DISTRIBUITA " + media + " al Gateway");
        comunicatoreConGateway.InviaNuovaMediaAlGateway(media);
    }

    private double CalcolaMediaDistribuita(){
        System.out.println("Calcolo la media distribuita del quartiere:");
        double media = 0;
        for (Messaggi.Token.Media m: messaggioToken.getMediaList()){
            System.out.println("Media nodo con id " + m.getIdNodo() + ": " + m.getMedia());
            media += m.getMedia();
        }
        return media/messaggioToken.getMediaList().size();
    }

    public void ResettaToken(){
        messaggioToken = null;
    }

    public void EliminaNodoDalGateway(){
        comunicatoreConGateway.EliminaNodoDalGateway();
    }

    public void EliminaNodiUsciti(){ comunicatoreTraNodi.EliminaNodiUsciti(); }

    public void EliminatiDallaListaNodiDeiVicini(){
        comunicatoreTraNodi.EliminatiDallaListaNodiDeiVicini();
    }

    public void TerminaComponenti(){
        sensore.stop();
        comunicatoreTraNodi.TerminaServiziGrpc();
    }
}