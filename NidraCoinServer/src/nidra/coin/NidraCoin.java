/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import java.io.PrintStream;

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
               BlockChain bc = new BlockChain();
               new NodeServer(bc,vista);
//               bc.createTransaction(new Transactions("adrian","gerardo",65));
//               bc.createTransaction(new Transactions("venganito","juanito",50));
//                System.out.println("MINANDO...");
//                bc.minePendingTransactions("juanito");
                System.out.println("balance="+bc.getBalanceofAddress("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIVm4lSY8jAxo5M8IvkKWxRBgMSSejmMFnMv7Eqd2oHUFJtI1DVpY0q2JBP779CDeBtwAJ4somijzOXMhwJEDZUCAwEAAQ=="));
            }
        };
        thread1.start();
//        Thread thread2 = new Thread() {
//            @Override
//            public void run() {
//               new NodeServer(bc);
//            }
//        };
//        thread2.start();
        
        
    }
    
}
