package Progetto;

import Progetto.Eccezioni.ErroreScelta;
import Progetto.Gateway.Mediaquartiere;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ClientAnalista {

    public static void main(String[] args) {
        boolean continua = true, ok = false;
        int scelta = 0;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        while (continua){
            while(!ok){
                System.out.println("CHE INFORMAZIONI DESIDERI OTTENERE DAL GATEWAY?");
                System.out.println("- Inserire 1 per ottenere il numero dei nodi presenti nella rete");
                System.out.println("- Inserire 2 per ottenere le ultime N statistiche");
                System.out.println("- Inserire 3 per ottenere la deviazione standard e la media delle ultime N statistiche");
                System.out.println("- Inserire 4 per terminare");
                System.out.print("INSERISCI SCELTA: ");
                try {
                    scelta = Integer.parseInt(read.readLine());
                    if(scelta<1 || scelta>4)
                        throw new ErroreScelta();
                    ok = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (java.lang.NumberFormatException e){
                    System.out.println("Errore, deve essere inserito un numero positivo");
                } catch (ErroreScelta erroreScelta) {
                    System.out.println(erroreScelta.getMessage());
                }
            }
            switch (scelta){
                case 1:
                    NumeroDINodi();
                    ok = false;
                    break;
                case 2:
                    NumeroNStatistiche(InserimentoNcorretto(read));
                    ok = false;
                    break;
                case 3:
                    DeviazioneStandardEMediaNStatistiche(InserimentoNcorretto(read));
                    ok = false;
                    break;
                case 4:
                    continua = false;
                    break;
            }
        }
    }

    private static void NumeroDINodi(){
        WebResource webResource = Client.create().resource("http://localhost:5000/gateway/NumeroNodi");
        ClientResponse response = webResource.type("application/xml").get(ClientResponse.class);
        String json = response.getEntity(String.class);
        System.out.println("Numero di nodi presenti nella rete: " + json);
    }

    private static void NumeroNStatistiche(int n){
        ClientResponse response = postToGateway("http://localhost:5000/gateway/getNstatistiche", n);
        if(Successo(response,n)){
            Gson gson = new Gson();
            String json = response.getEntity(String.class);
            List<Mediaquartiere> medie = gson.fromJson(json, new TypeToken<List<Mediaquartiere>>(){}.getType());
            for (Mediaquartiere m : medie)
                System.out.println(m.toString());
        }
        else
            System.out.println("Il gateway non ha ancora immagazzinato " + n + " statistiche");
    }

    private static void DeviazioneStandardEMediaNStatistiche(int n){
        ClientResponse response = postToGateway("http://localhost:5000/gateway/getNdev", n);
        if(Successo(response,n)){
            String str = response.getEntity(String.class);
            String[] valori = str.split(";");
            System.out.println("Media: " + valori[0] + ", Deviazione Standard: " + valori[1]);
        }
        else
            System.out.println("Il gateway non ha ancora immagazzinato " + n + " statistiche");
    }

    private static int InserimentoNcorretto( BufferedReader read){
        while (true) {
            System.out.print("N: ");
            try {
                return Integer.parseInt(read.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.lang.NumberFormatException e) {
                System.out.println("Errore, deve essere inserito un numero positivo");
            }
        }
    }

    private static ClientResponse postToGateway(String url, int n){
        WebResource webResource = Client.create().resource(url);
        String input = Integer.toString(n);
        return webResource.type("text/plain").post(ClientResponse.class,input);
    }

    private static boolean Successo(ClientResponse response, int n){
        int stato = response.getStatus();
        return stato != 406;
    }
}
