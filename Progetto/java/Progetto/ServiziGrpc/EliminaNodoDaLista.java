package Progetto.ServiziGrpc;

import Progetto.EliminaNodoDaListaGrpc.*;
import Progetto.Messaggi;
import Progetto.Nodo.Nodo;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class EliminaNodoDaLista extends EliminaNodoDaListaImplBase {

    Nodo nodo;

    public EliminaNodoDaLista(Nodo nodo) {
        this.nodo = nodo;
    }

    @Override
    public void eliminaNodoDaLista(Messaggi.ListaNodi request, StreamObserver<Messaggi.Risposta> responseObserver) {
        int i=0;
        boolean trovato = false;
        ArrayList<Nodo> nuovaLista = new ArrayList<>();
        System.out.print("Lista nodo + prec o succ: ");
        for (Messaggi.ListaNodi.Nodo n: request.getNodoList()){
            System.out.print(n.getIdNodo() + " ");
            nuovaLista.add(new Nodo(n.getIdNodo(),n.getPortaNodo()));
        }
        System.out.println();

        boolean sostituisciNodoSuccessivo = true;
        for (Nodo n: nodo.listaNodi){
            if(n.getIdnodo() == nuovaLista.get(0).getIdnodo()){
                for (Nodo no: nodo.listaNodi){
                    if(no.getIdnodo() == nuovaLista.get(1).getIdnodo()){
                        sostituisciNodoSuccessivo = false;
                        break;
                    }
                }
                nodo.listaNodiEliminati.add(n);
                if(sostituisciNodoSuccessivo){
                    nodo.listaNodi.set(i,nuovaLista.get(1));
                    System.out.println("Sostituisco il nodo " + nodo.listaNodi.get(i).getIdnodo() + " con il nodo " + nuovaLista.get(1));
                }
                else{
                    System.out.println("Rimuovo il nodo " + nodo.listaNodi.get(i).getIdnodo());
                    nodo.listaNodi.remove(i);
                }

                System.out.println("Il nodo vicino " + nuovaLista.get(0).getIdnodo() + " è uscito dalla rete");
                Messaggi.Risposta risposta = Messaggi.Risposta.newBuilder()
                        .setRisposta("FROM NODO " + nodo.getIdnodo() + ": OK, Il nodo è stato eliminato dal vicino")
                        .build();
                responseObserver.onNext(risposta);
                responseObserver.onCompleted();
                trovato = true;
                break;
            }
            i++;
        }
        if(!trovato){
            Messaggi.Risposta risposta = Messaggi.Risposta.newBuilder()
                    .setRisposta("FROM NODO " + nodo.getIdnodo() + ": Attenzione, il vicino non ti ha nella sua lista")
                    .build();
            responseObserver.onNext(risposta);
            responseObserver.onCompleted();
        }
    }
}
