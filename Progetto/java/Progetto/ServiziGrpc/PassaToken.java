package Progetto.ServiziGrpc;

import Progetto.Messaggi;
import Progetto.Nodo.Nodo;
import Progetto.PassaTokenGrpc.*;
import io.grpc.stub.StreamObserver;

public class PassaToken extends PassaTokenImplBase {

    private Nodo nodo;

    public PassaToken(Nodo nodo){
        this.nodo = nodo;
    }

    @Override
    public void passaToken(Messaggi.Token request, StreamObserver<Messaggi.Risposta> responseObserver) {
        nodo.messaggioToken = request;
        synchronized (nodo.lockToken){
            nodo.token = true;
            nodo.lockToken.notify();
        }
        Messaggi.Risposta risposta = Messaggi.Risposta.newBuilder()
                .setRisposta("FROM NODO " + nodo.getIdnodo() + ": OK, Il Token è stato inviato")
                .build();
        responseObserver.onNext(risposta);
        responseObserver.onCompleted();
    }
}
