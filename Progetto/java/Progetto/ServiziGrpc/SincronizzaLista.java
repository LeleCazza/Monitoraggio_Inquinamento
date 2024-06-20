package Progetto.ServiziGrpc;

import Progetto.Messaggi;
import Progetto.Nodo.Nodo;
import Progetto.SincronizzaListaGrpc.*;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;

public class SincronizzaLista extends SincronizzaListaImplBase {

    private final Nodo nodo;
    public SincronizzaLista(Nodo nodo){
        this.nodo = nodo;
    }

    @Override
    public void sincronizzaLista(Messaggi.ListaNodi request, StreamObserver<Messaggi.Risposta> responseObserver) {
        ArrayList<Nodo> nuovaLista = new ArrayList<>();
        for (Messaggi.ListaNodi.Nodo n: request.getNodoList())
            nuovaLista.add(new Nodo(n.getIdNodo(),n.getPortaNodo()));

        System.out.print("Sostituisco la vecchia lista: ");
        for (Nodo n : nodo.listaNodi)
            System.out.print(n.getIdnodo() + " ");
        System.out.println();
        System.out.print("Con la nuova lista: ");
        for (Nodo n : nuovaLista)
            System.out.print(n.getIdnodo() + " ");
        System.out.println();
        nodo.listaNodi = nuovaLista;
        Messaggi.Risposta risposta = Messaggi.Risposta.newBuilder()
                .setRisposta("FROM NODO " + nodo.getIdnodo() + ": OK, La lista è stata sincronizzata")
                .build();
        responseObserver.onNext(risposta);
        responseObserver.onCompleted();
    }
}
