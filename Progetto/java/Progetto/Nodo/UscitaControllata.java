package Progetto.Nodo;

import java.awt.event.*;
import javax.swing.*;

public class UscitaControllata extends Thread{

    Nodo nodo;

    public UscitaControllata(Nodo nodo) {
        this.nodo = nodo;
    }

    @Override
    public void run() {
        JFrame f = new JFrame("NODO " + nodo.getIdnodo());
        JButton b= new JButton("ESCI");
        b.setBounds(50,10,100,50);
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                synchronized (nodo.lockEsciDallaRete){
                    nodo.esciDallaRete = true;
                }
            }
        });
        f.add(b);
        f.setSize(200,110);
        f.setLayout(null);
        f.setVisible(true);
    }
}