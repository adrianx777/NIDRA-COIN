/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidracoinminer;

import Pack1.Transactions;
import Pack1.Block;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class MinerA implements ActionListener {

    String ip;
    int port;
    String wallet;
    VistaMinero vista;
    boolean activo = false;
    Block lastblock;
    Block block;
    Socket sc;
    int mined = 0;
    ArrayList<Transactions> pending;
    int diff = 2;

    public MinerA() {
        vista = new VistaMinero();
        vista.setTitle("Miner");//Titulo de la ventana
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
        vista.connectb.addActionListener(this);
        pending = new ArrayList<>();
    }

    private void benchmark() {
        vista.hashrate.setText("Calculando... Hash/s");
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        block = new Block(timestamp, this.pending, null);
        block.setPreviousHash(this.getlastblock().getHash());
        ArrayList<Double> tim = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long inicio = System.nanoTime();
            block.calchash();
            long fin = System.nanoTime();
            double time = (double) (fin - inicio) / 1000000;
            tim.add(time);
        }
        double x = 0;
        for (Double d : tim) {
            x += d;
        }
        System.out.println("x=" + x);
        x = 1000 / (x / 100);
        vista.hashrate.setText(x + " Hash/s");
    }

    private void Server() {
        try {
            sc = new Socket(ip, port);
            DataInputStream fa = new DataInputStream(sc.getInputStream());
            String pm = fa.readUTF();
            vista.connected.setText("Yes");
            vista.log.setText(pm);
            requestblock();
            benchmark();
            Thread thread1 = new Thread() {
                @Override
                public void run() {
//                    Get();
                }
            };
            Thread thread2 = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            minePendingTransactions();
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MinerA.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            thread1.start();
            thread2.start();

        } catch (IOException ex) {
            Logger.getLogger(MinerA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void minePendingTransactions() {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        block = new Block(timestamp, this.pending, null);
        block.setPreviousHash(this.getlastblock().getHash());
        block.Mine(diff, lastblock);
        if (validatehash(block)) {
            sendblock(block);
//            System.out.println("BLOQUE MINADO!");
        } else {
            System.out.println("invalido");

        }
    }

    private void sendblock(Block block) {
        ObjectOutputStream oos = null;
        try {
            DataOutputStream fs = new DataOutputStream(sc.getOutputStream());
            fs.writeUTF("MinedBlock" + "EOF");
            oos = new ObjectOutputStream(fs);
            oos.writeObject(block);
            oos.writeObject(wallet);
            requestblock();
        } catch (IOException ex) {
            Logger.getLogger(MinerA.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    }

    public Block getlastblock() {
        return this.lastblock;
    }

    public boolean validatehash(Block block) {
        if (block.calchash().equals(block.getHash()) && getlastblock().getHash().equals(block.getPreviousHash())) {
            if (block.getTransactions() != null) {
                return true;
            }
        }
        return false;
    }

    private void requestblock() {
        try {
//            Socket news = new Socket(ip,port);
            System.out.println("BLOCK");
            DataOutputStream fs = new DataOutputStream(sc.getOutputStream());
            fs.writeUTF("GetBlock" + "EOF");
            DataInputStream is = new DataInputStream(sc.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(is);
            ArrayList<Object> obj = (ArrayList<Object>) ois.readObject();
            String lastmensaje = (String) ois.readObject();
            vista.log.setText(vista.log.getText() + "\n" + lastmensaje);
            lastblock = (Block) obj.get(0);
            pending = (ArrayList) obj.get(1);
            diff = (int) obj.get(2);

            if (lastmensaje.contains(vista.wallet.getText())) {
                    mined += 1;
                    vista.mined.setText(String.valueOf(mined));
                }
                System.out.println("diff=" + diff);

            }catch (IOException ex) {
            System.out.println("Error");
            Logger.getLogger(MinerA.class.getName()).log(Level.SEVERE, null, ex);
        }catch (ClassNotFoundException ex) {
            Logger.getLogger(MinerA.class.getName()).log(Level.SEVERE, null, ex);
        }
        }

//    private void Get() {
//        while (true) {
//            try {
//                DataInputStream fa = new DataInputStream(sc.getInputStream());
//                String pm = fa.readUTF();
//                if (pm.contains(vista.wallet.getText())) {
//                    mined += 1;
//                    vista.mined.setText(String.valueOf(mined));
//                }
//                if (pm.contains(block.getHash())) {
//                    System.out.println("requestblock");
//                    requestblock();
//                }
//                vista.log.setText(vista.log.getText() + "\n" + pm);
//            } catch (IOException ex) {
////                Logger.getLogger(MinerA.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
        @Override
        public void actionPerformed
        (ActionEvent e
        
            ) {
        if (e.getSource() == vista.connectb && activo == false) {
                vista.ip.setEnabled(false);
                vista.port.setEnabled(false);
                vista.wallet.setEnabled(false);
                vista.connectb.setEnabled(false);
                this.ip = vista.ip.getText();
                this.port = Integer.valueOf(vista.port.getText());
                this.wallet = vista.wallet.getText();
                Thread thread1 = new Thread() {
                    @Override
                    public void run() {
                        Server();
                    }
                };
                thread1.start();
            }

        }

    }
