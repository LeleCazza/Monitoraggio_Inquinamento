package Progetto.Gateway;

import Progetto.Eccezioni.ErroreMancanzaNstatistiche;
import Progetto.Nodo.Nodo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Gateway {

    @XmlElement(name="nodi")
    private final ArrayList<Nodo> listaNodi;
    @XmlElement(name="medie")
    private final ArrayList<Mediaquartiere> medieQuartiere;
    private final Object lockPortaDaAssegnare = "LOCK";
    private int portaDaAssegnare = 5001;

    private static Gateway instance;

    public synchronized static Gateway getInstance(){
        if(instance==null)
            instance = new Gateway();
        return instance;
    }
    private Gateway() {
        listaNodi = new ArrayList<>();
        medieQuartiere = new ArrayList<>();
    }

    public synchronized int addNodo(Nodo nodo){
        ArrayList<Nodo> copiaLocaleListaNodi;
        synchronized (listaNodi){
            copiaLocaleListaNodi = new ArrayList<>(listaNodi);
        }
        for (Nodo n: copiaLocaleListaNodi) {
            if(n.getIdnodo() == nodo.getIdnodo())
                return 0;
        }
        synchronized (lockPortaDaAssegnare){
            nodo.setPortanodo(portaDaAssegnare);
            portaDaAssegnare++;
        }
        synchronized (listaNodi){
            listaNodi.add(nodo);
            Collections.sort(listaNodi);
        }
        return nodo.getPortanodo();
    }

    public boolean addMedia(Mediaquartiere mediaQuartiere){
        try{
            synchronized (medieQuartiere){
                medieQuartiere.add(mediaQuartiere);
            }
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Nodo> getListaNodi(){
        synchronized (listaNodi){
            return listaNodi;
        }
    }

    public boolean removeNodo(Nodo nodo) {
        ArrayList<Nodo> copiaLocaleListaNodi;
        synchronized (listaNodi){
            copiaLocaleListaNodi = new ArrayList<>(listaNodi);
        }
        int i = 0;
        for (Nodo n : copiaLocaleListaNodi) {
            if (n.getIdnodo() == nodo.getIdnodo()){
                synchronized (listaNodi){
                    listaNodi.remove(i);
                }
                return true;
            }
            else
                i++;
        }
        return false;
    }

    public int getNumeroNodi(){
        synchronized (listaNodi){
            return listaNodi.size();
        }
    }

    public List<Mediaquartiere> getUltimeStatistiche(String n) throws ErroreMancanzaNstatistiche {
        ArrayList<Mediaquartiere> copiaLocaleListaMedie;
        synchronized (medieQuartiere){
            copiaLocaleListaMedie = new ArrayList<>(medieQuartiere);
        }
        if(copiaLocaleListaMedie.size() < Integer.parseInt(n) || Integer.parseInt(n) <= 0)
            throw new ErroreMancanzaNstatistiche();
        else
            return  copiaLocaleListaMedie.subList(copiaLocaleListaMedie.size()-Integer.parseInt(n),copiaLocaleListaMedie.size());
    }

    public String getUltimeDev(String n) throws ErroreMancanzaNstatistiche {
        int N = Integer.parseInt(n);
        ArrayList<Mediaquartiere> copiaLocaleListaMedie;
        synchronized (medieQuartiere){
            copiaLocaleListaMedie = new ArrayList<>(medieQuartiere);
        }
        if(copiaLocaleListaMedie.size() < N || N <= 0)
            throw new ErroreMancanzaNstatistiche();
        else{
            double media = 0;
            double somma = 0;
            for(int i=1; i<=N; i++)
                media += copiaLocaleListaMedie.get(copiaLocaleListaMedie.size()-i).getMedia();
            media = media/N;
            for(int i=1; i<=N; i++)
                somma += Math.pow(copiaLocaleListaMedie.get(copiaLocaleListaMedie.size()-i).getMedia() - media,2);
            double deviazioneStandard = Math.sqrt(somma/N);
            return media + ";" + deviazioneStandard;
        }
    }
}