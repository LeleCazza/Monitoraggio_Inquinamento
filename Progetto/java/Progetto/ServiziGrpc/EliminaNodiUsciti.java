package Progetto.ServiziGrpc;

import Progetto.EliminaNodiUscitiGrpc.*;
import Progetto.Messaggi;
import Progetto.Nodo.Nodo;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;

public class EliminaNodiUsciti extends EliminaNodiUscitiImplBase {

    Nodo nodo;

    public EliminaNodiUsciti(Nodo nodo) {
        this.nodo = nodo;
    }

    @Override
    public void eliminaNodiUsciti(Messaggi.ListaNodi request, StreamObserver<Messaggi.Risposta> responseObserver) {
        ArrayList<Nodo> nuovaLista = new ArrayList<>();

        for (Messaggi.ListaNodi.Nodo n: request.getNodoList())
            nuovaLista.add(new Nodo(n.getIdNodo(),n.getPortaNodo()));

        int i=0;
        for (Nodo n: nodo.listaNodi){
            for(Nodo nodoDaEliminare : nuovaLista){
                if(n.getIdnodo() == nodoDaEliminare.getIdnodo()){
                    nodo.listaNodi.remove(i);
                    System.out.println("Eliminato nodo " + nodoDaEliminare.getIdnodo() + " poichè già uscito ");
                    break;
                }
            }
            i++;
        }

        Messaggi.Risposta risposta = Messaggi.Risposta.newBuilder()
                .setRisposta("FROM NODO " + nodo.getIdnodo() + ": OK, I nodi già usciti ancora in memoria del vicino sono stati eliminati")
                .build();
        responseObserver.onNext(risposta);
        responseObserver.onCompleted();
        }
}