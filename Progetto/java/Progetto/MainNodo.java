package Progetto;

import Progetto.Eccezioni.ErroreIdNodo;
import Progetto.Nodo.Nodo;

public class MainNodo {

    private static boolean sonoNuovo = true;
    private static Nodo nodo;

    public static void main(String[] args) {
        nodo = new Nodo();
        ProvaAEntrareNellaRete();
        if(nodo.SonoDaSoloNellaRete())
            nodo.token = true;
        nodo.AvviaSensore();
        nodo.AvviaServerPerComunicazioneTraNodi();
        while (true) {
            synchronized (nodo.lockEsciDallaRete) {
                if (!nodo.esciDallaRete) {
                    synchronized (nodo.lockToken) {
                        if (nodo.token) {
                            sonoNuovo = false;
                            nodo.AttendiNuovaMediaLocale();
                            if (nodo.SonoDaSoloNellaRete())
                                nodo.InviaMediaLocaleAlGateway();
                            else {
                                if (nodo.TokenConMedieCompleto()) {
                                    nodo.InviaMediaDistribuitaAlGateway();
                                    nodo.ResettaToken();
                                }
                                nodo.PassaTokenAlSuccessivo();
                            }
                        } else {
                            if (sonoNuovo) {
                                nodo.SincronizzaListaNodiConVicini();
                                sonoNuovo = false;
                            }
                            nodo.AttendiToken();
                        }
                    }
                } else {
                    if(nodo.SonoDaSoloNellaRete()){
                        nodo.EliminaNodoDalGateway();
                    }
                    else{
                        nodo.AttendiToken();
                        if(nodo.SonoDaSoloNellaRete()){
                            nodo.EliminaNodoDalGateway();
                        }
                        else{
                            nodo.EliminaNodoDalGateway();
                            nodo.EliminatiDallaListaNodiDeiVicini();
                            nodo.EliminaNodiUsciti();
                            if (nodo.TokenConMedieCompleto())
                                nodo.InviaMediaDistribuitaAlGateway();
                            nodo.ResettaToken();
                            nodo.PassaTokenAlSuccessivo();
                        }
                    }
                    nodo.TerminaComponenti();
                    System.exit(0);
                }
            }
        }
    }

    public static void ProvaAEntrareNellaRete(){
        nodo.ImpostaIdDaConsole();
        try {
            nodo.EntraNellaRete();
        } catch (ErroreIdNodo e) {
            System.out.println(e.getMessage());
            ProvaAEntrareNellaRete();
        }
    }
}