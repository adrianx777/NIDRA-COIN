/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class NidraCoin {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VistaServer vista = new VistaServer();
        vista.setTitle("Server");//Titulo de la ventana
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
        PrintStream printStream = new PrintStream(new CustomOutputStream(vista.log));
        System.setOut(printStream);
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                BlockChain bc = new BlockChain(vista);
                new NodeServer(bc, vista);
//               bc.createTransaction(new Transactions("adrian","gerardo",65));
//               bc.createTransaction(new Transactions("venganito","juanito",50));
//                System.out.println("MINANDO...");
//                bc.minePendingTransactions("juanito");
                System.out.println("balance=" + bc.getBalanceofAddress("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIVm4lSY8jAxo5M8IvkKWxRBgMSSejmMFnMv7Eqd2oHUFJtI1DVpY0q2JBP779CDeBtwAJ4somijzOXMhwJEDZUCAwEAAQ=="));
            }
        };
        thread1.start();
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                int hh = 0;
                int mm = 0;
                int ss = 0;
                
                String hht = "";
                String mmt = "";
                String sst = "";
                while (true) {
                    ss++;
                    if (ss>=60){
                        ss = 0;
                        mm++;
                    }
                    if (mm>=60){
                        mm = 0;
                        hh++;
                    }
                    if (ss<10){ sst="0"+ss; }else{sst=""+ss;}
                    if (mm<10){ mmt="0"+mm; }else{mmt=""+mm;}
                    if (hh<10){ hht="0"+hh; }else{hht=""+hh;}
                    
                    vista.working.setText("Working for: "+hht+":"+mmt+":"+sst);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NidraCoin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        thread2.start();

    }

}
