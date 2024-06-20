package Progetto.Nodo;

import Progetto.Simulatore.Buffer;
import Progetto.Simulatore.Measurement;
import Progetto.Simulatore.PM10Simulator;

public class Sensore implements Buffer {

    private final Measurement[] buffer = {null,null,null,null,null,null,null,null,null,null,null,null};
    private final Nodo nodo;
    private int indiceMeasurement = 0;
    private PM10Simulator simulator;

    public Sensore(Nodo nodo){
        this.nodo = nodo;
    }

    public void start(){
        simulator = new PM10Simulator(this);
        simulator.start();
    }

    public void stop(){
        simulator.stopMeGently();
    }

    @Override
    public void addMeasurement(Measurement measurement) {
        if(indiceMeasurement%12 == (6)){
            double mediaBuffer = 0;
            for (Measurement m: buffer) {
                if(m == null)
                    continue;
                mediaBuffer += m.getValue();
            }
            synchronized (nodo.lockMediaLocale) {
                if (nodo.mediaLocale == 0){
                    nodo.mediaLocale = mediaBuffer / 6;
                    System.out.println("mediaBuffer: " + mediaBuffer/6);
                }
                else{
                    nodo.mediaLocale = (nodo.mediaLocale + (mediaBuffer/12)) / 2;
                    System.out.println("mediaBuffer: " + mediaBuffer/12);
                }
                synchronized (nodo.lockNuovaMedia){
                    nodo.nuovaMedia = true;
                    nodo.lockNuovaMedia.notify();
                }
                System.out.println("mediaLocale: " + nodo.mediaLocale);
            }
        }
        buffer[indiceMeasurement%12] = measurement;
        //System.out.println("inserisco " + indiceMeasurement + " misura nel buffer: " + measurement);
        indiceMeasurement++;
    }
}
