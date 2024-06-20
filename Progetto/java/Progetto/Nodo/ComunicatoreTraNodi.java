package Progetto.Nodo;

import Progetto.*;
import Progetto.ServiziGrpc.EliminaNodiUsciti;
import Progetto.ServiziGrpc.EliminaNodoDaLista;
import Progetto.ServiziGrpc.PassaToken;
import Progetto.ServiziGrpc.SincronizzaLista;
import com.google.gson.internal.$Gson$Preconditions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class ComunicatoreTraNodi {

    private final Nodo nodo;
    private Server server;

    public ComunicatoreTraNodi(Nodo nodo){ this.nodo = nodo; }

    public void AvviaServiziGrpc(){
        server = ServerBuilder.forPort(nodo.getPortanodo())
                .addService(new SincronizzaLista(nodo))
                .addService(new PassaToken(nodo))
                .addService(new EliminaNodoDaLista(nodo))
                .addService(new EliminaNodiUsciti(nodo))
                .build();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void TerminaServiziGrpc(){
        server.shutdown();
    }

    public void SincronizzaListaNodi(){
            int dimensioneLista = nodo.listaNodi.size();
            int posizioneNodo = posizioneNodoNellaLista();
            int portaPrec = portaNodoPrecedente(posizioneNodo,dimensioneLista);
            int portaSucc = portaNodoSuccessivo(posizioneNodo,dimensioneLista);
            sendMessaggioDiSincronizzazione(portaPrec);
            if(dimensioneLista!=2)
                sendMessaggioDiSincronizzazione(portaSucc);
    }

    private int posizioneNodoNellaLista(){
        int posizioneNellaLista=0;
        for (Nodo n: nodo.listaNodi) {
            if(n.getIdnodo() == nodo.getIdnodo())
                break;
            posizioneNellaLista++;
        }
        return posizioneNellaLista;
    }

    private int portaNodoPrecedente(int posizioneNodo, int dimensioneLista){
        int porta;
        if(posizioneNodo - 1 < 0)
            porta = nodo.listaNodi.get((dimensioneLista - 1) % dimensioneLista).getPortanodo();
        else
            porta = nodo.listaNodi.get((posizioneNodo - 1) % dimensioneLista).getPortanodo();
        System.out.println("Il nodo prima di me si trova alla porta: " + porta);
        return porta;
    }

    private int portaNodoSuccessivo(int posizioneNodo, int dimensioneLista){
        int porta = nodo.listaNodi.get((posizioneNodo + 1) % dimensioneLista).getPortanodo();
        System.out.print("Lista dei nodi: ");
        for (Nodo n: nodo.listaNodi){
            System.out.print(n.getIdnodo() + " ");
        }
        System.out.println();
        System.out.println("Il nodo dopo di me si trova alla porta: " + porta);
        return porta;
    }

    private void sendMessaggioDiSincronizzazione(int portaNodo){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:"+portaNodo)
                .usePlaintext(true)
                .build();
        Messaggi.ListaNodi.Builder listaToSend = Messaggi.ListaNodi.newBuilder();
        for(Nodo n: nodo.listaNodi){
            listaToSend.addNodo(Messaggi.ListaNodi.Nodo.newBuilder().setIdNodo(n.getIdnodo()).setPortaNodo(n.getPortanodo()));
        }
        SincronizzaListaGrpc.SincronizzaListaBlockingStub stub = SincronizzaListaGrpc.newBlockingStub(channel);
        Messaggi.Risposta risposta = stub.sincronizzaLista(listaToSend.build());
        System.out.println(risposta.getRisposta());
        channel.shutdown();
    }

    public void PassaTokenAlSuccessivo(){
        int dimensioneLista = nodo.listaNodi.size();
        int posizioneNodo = posizioneNodoNellaLista();
        int portaSucc = portaNodoSuccessivo(posizioneNodo,dimensioneLista);
        Messaggi.Token token;
        if(nodo.messaggioToken != null)
            token = addMediaToMessaggioToken();
        else
            token = createNewToken();
        sedMessaggioToken(portaSucc, token);
        nodo.token = false;
    }

    private Messaggi.Token addMediaToMessaggioToken(){
        return nodo.messaggioToken.toBuilder().addMedia(Messaggi.Token.Media.newBuilder()
                .setIdNodo(nodo.getIdnodo()).setMedia(nodo.mediaLocale)).build();
    }

    private Messaggi.Token createNewToken(){
        nodo.messaggioToken = Messaggi.Token.newBuilder().addMedia(Messaggi.Token.Media.newBuilder()
                .setIdNodo(nodo.getIdnodo()).setMedia(nodo.mediaLocale)).build();
        return nodo.messaggioToken;
    }

    private void sedMessaggioToken(int idPorta, Messaggi.Token token){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:"+idPorta)
                .usePlaintext(true)
                .build();
        PassaTokenGrpc.PassaTokenBlockingStub stub = PassaTokenGrpc.newBlockingStub(channel);
        Messaggi.Risposta risposta = stub.passaToken(token);
        System.out.println(risposta.getRisposta());
        channel.shutdown();
    }

    public void EliminatiDallaListaNodiDeiVicini(){
        int dimensioneLista = nodo.listaNodi.size();
        int posizioneNodo = posizioneNodoNellaLista();
        int portaSucc = portaNodoSuccessivo(posizioneNodo,dimensioneLista);
        int portaPrec = portaNodoPrecedente(posizioneNodo,dimensioneLista);
        System.out.print("Lista dei nodi: ");
        for (Nodo n: nodo.listaNodi){
            System.out.print(n.getIdnodo() + " ");
        }
        System.out.println();
        Nodo nodoSuccessivo = new Nodo(nodo.listaNodi.get((posizioneNodo+1)%dimensioneLista).getIdnodo(),portaSucc);
        int idPrec;
        if(posizioneNodo - 1 < 0)
            idPrec = nodo.listaNodi.get((dimensioneLista - 1) % dimensioneLista).getIdnodo();
        else
            idPrec = nodo.listaNodi.get((posizioneNodo - 1) % dimensioneLista).getIdnodo();
        Nodo nodoPrecedente = new Nodo(idPrec,portaPrec);
        sedMessaggioDelete(portaSucc, nodoPrecedente);
        sedMessaggioDelete(portaPrec, nodoSuccessivo);
    }

    private void sedMessaggioDelete(int idPorta, Nodo nodo){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:"+idPorta)
                .usePlaintext(true)
                .build();
        EliminaNodoDaListaGrpc.EliminaNodoDaListaBlockingStub stub = EliminaNodoDaListaGrpc.newBlockingStub(channel);
        Messaggi.Risposta risposta = stub.eliminaNodoDaLista(Messaggi.ListaNodi.newBuilder()
                        .addNodo(Messaggi.ListaNodi.Nodo.newBuilder()
                                .setIdNodo(this.nodo.getIdnodo()).setPortaNodo(this.nodo.getPortanodo()))
                        .addNodo(Messaggi.ListaNodi.Nodo.newBuilder()
                                .setIdNodo(nodo.getIdnodo()).setPortaNodo(nodo.getPortanodo()))
                        .build());
        System.out.println(risposta.getRisposta());
        channel.shutdown();
    }

    public void EliminaNodiUsciti(){
        int dimensioneLista = nodo.listaNodi.size();
        int posizioneNodo = posizioneNodoNellaLista();
        int portaSucc = portaNodoSuccessivo(posizioneNodo,dimensioneLista);
        int portaPrec = portaNodoPrecedente(posizioneNodo,dimensioneLista);
        sedMessaggioDeleteEliminati(portaSucc);
        sedMessaggioDeleteEliminati(portaPrec);
    }

    private void sedMessaggioDeleteEliminati(int idPorta){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:"+idPorta)
                .usePlaintext(true)
                .build();
        EliminaNodiUscitiGrpc.EliminaNodiUscitiBlockingStub stub = EliminaNodiUscitiGrpc.newBlockingStub(channel);
        Messaggi.ListaNodi.Builder listaToSend = Messaggi.ListaNodi.newBuilder();
        for(Nodo n: nodo.listaNodiEliminati){
            listaToSend.addNodo(Messaggi.ListaNodi.Nodo.newBuilder().setIdNodo(n.getIdnodo()).setPortaNodo(n.getPortanodo()));
        }
        Messaggi.Risposta risposta = stub.eliminaNodiUsciti(listaToSend.build());
        System.out.println(risposta.getRisposta());
        channel.shutdown();
    }
}