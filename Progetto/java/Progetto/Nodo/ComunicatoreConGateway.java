package Progetto.Nodo;

import Progetto.Eccezioni.ErroreIdNodo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComunicatoreConGateway {

    private final Nodo nodo;

    public ComunicatoreConGateway(Nodo nodo) {
        this.nodo = nodo;
    }

    public void AddNodoToGateway() throws ErroreIdNodo {
        WebResource webResource = Client.create().resource("http://localhost:5000/gateway/addNodo");
        sendPostToGateway(webResource,nodo.getIdnodo());
    }

    private void sendPostToGateway(WebResource webResource, int idNodo) throws ErroreIdNodo {
        String input = "<nodo><idnodo>"+idNodo+"</idnodo><portanodo>"+idNodo+"</portanodo></nodo>";
        ClientResponse response = webResource.type("application/xml").post(ClientResponse.class,input);
        if (response.getStatus() == 409)
            throw new ErroreIdNodo();
        else{
            int portaNodo = Integer.parseInt(response.getEntity(String.class));
            System.out.println("Al nodo è stata assegnata la porta: " + portaNodo);
            nodo.setPortanodo(portaNodo);
        }
    }

    public ArrayList<Nodo> GetListaNodiFromGateway(){
        WebResource webResource = Client.create().resource("http://localhost:5000/gateway/listaNodi");
        ArrayList<Nodo> listaNodi = getListNodiFromGateway(webResource);
        System.out.println("Lista dei nodi presenti nella rete:");
        for (Nodo n: listaNodi) {
            System.out.print("Id Nodo: " + n.getIdnodo());
            System.out.println(", Porta Nodo: " + n.getPortanodo());
        }
        return listaNodi;
    }

    private ArrayList<Nodo> getListNodiFromGateway (WebResource webResource){
        ClientResponse response = webResource.type("application/xml").get(ClientResponse.class);
        String json = response.getEntity(String.class);
        ArrayList<Nodo> listaNodi = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(json);
        int i=0;
        int idNodo = 0;
        while(matcher.find()){
            if(i%2==0)
                idNodo = Integer.parseInt(matcher.group());
            else
                listaNodi.add(new Nodo(idNodo,Integer.parseInt(matcher.group())));
            i++;
        }
        return listaNodi;
    }

    public void InviaNuovaMediaAlGateway(double media){
        WebResource webResource = Client.create().resource("http://localhost:5000/gateway/addMedia");
        String input = "<mediaquartiere>" +
                "<media>" +
                media +
                "</media>" +
                "<timestamp>" +
                new Timestamp(System.currentTimeMillis()).toString() +
                "</timestamp>" +
                "</mediaquartiere>";
        ClientResponse response = webResource.type("application/xml").post(ClientResponse.class,input);
        System.out.println("Il Gateway risponde: " + response.getStatusInfo());
    }

    public void EliminaNodoDalGateway(){
        WebResource webResource = Client.create().resource("http://localhost:5000/gateway/rmNodo");
        int stato = sendDeleteToGateway(webResource,nodo);
        if(stato == 200)
            System.out.println("Il nodo è stato eliminato dal Gateway");
    }

    private int sendDeleteToGateway(WebResource webResource, Nodo nodo){
        String input = "<nodo><idnodo>"+nodo.getIdnodo()+"</idnodo><portanodo>"+nodo.getPortanodo()+"</portanodo></nodo>";
        ClientResponse response = webResource.type("application/xml").post(ClientResponse.class,input);
        return response.getStatus();
    }
}
