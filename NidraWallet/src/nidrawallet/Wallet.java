/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidrawallet;

import Pack1.Cifrado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author adria
 */
public class Wallet implements ActionListener {

    VistaWallet vista;
    boolean activos = false;
    String PuKey = "";
    String Prkey = "";
    String ip = "ec2-18-212-12-245.compute-1.amazonaws.com";
    final int puerto = 9002;
    public Wallet() {
        vista = new VistaWallet();
        vista.setTitle("Wallet");//Titulo de la ventana
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
        vista.sendb.addActionListener(this);
        File file = new File("info.txt");
        try {
            String strCurrentLine;
            BufferedReader br = new BufferedReader(new FileReader(file));
            int i = 1;
            while ((strCurrentLine = br.readLine()) != null) {
                if (i == 1) {
                    PuKey = strCurrentLine;
                } else if (i == 2) {
                    Prkey = strCurrentLine;
                } else {
                    break;
                }
                i++;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        vista.walletc.setText(PuKey);
        GetBalance(true);
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5000);
                        GetBalance(false);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thread1.start();
//        } catch (IOException ex) {
//            System.out.println("Error en la conexion");
//            vista.log.setText(vista.log.getText() + "\n" + "Error en la conexion");
////            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private double GetBalance(boolean show) {
        DataOutputStream fs = null;
        double nr = 0;
        Socket sc = null;
        try {
            sc = new Socket(ip, puerto);
            DataInputStream fa = new DataInputStream(sc.getInputStream());
            String pm = fa.readUTF();
            if (show == true) {
                vista.log.setText("Updating Balance...");
            }
            fs = new DataOutputStream(sc.getOutputStream());
            fs.writeUTF("GetBalance" + "EOF");
            fs.writeUTF(this.PuKey);
//            DataInputStream fa = new DataInputStream(sc.getInputStream());
            double b = fa.readDouble();
            fs.flush();
            System.out.println("bal=" + b);
            nr = b;
            vista.balance.setText(b + " NDC");
        } catch (IOException ex) {
            System.out.println("Error en la conexion");
            vista.log.setText(vista.log.getText() + "\n" + "Error en la conexion");
//            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (sc!=null){
                sc.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nr;
    }

    private ArrayList<String> separeinpars(String str) {
//        System.out.println("lenght="+str.length());
        ArrayList<String> nr = new ArrayList<>();
        nr.add(str.substring(0, 32));
        nr.add(str.substring(32, 64));
        nr.add(str.substring(64, 96));
        nr.add(str.substring(96, 128));
        return nr;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.sendb && activos == false) {
            activos = true;
            Socket sc = null;
            try {
                sc = new Socket(ip, puerto);
                DataInputStream fa = new DataInputStream(sc.getInputStream());
                String pm = fa.readUTF();
                vista.log.setText(vista.log.getText() + "\n" + pm);
            } catch (IOException ex) {
                System.out.println("Error en la conexion");
            vista.log.setText(vista.log.getText() + "\n" + "Error en la conexion");
//                Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
            }

            double balance = GetBalance(false);
            String fromwallet = vista.walletc.getText();
            String towallet = vista.towallet.getText();
            double tondc = Double.valueOf(vista.tondc.getText());
            if (balance > 0 && !fromwallet.equals("") && !towallet.equals("") && tondc > 0) {
                Cifrado cf = new Cifrado();
                PrivateKey prk = cf.Privatekeygen(this.Prkey);
                PublicKey puk = cf.Publickeygen(PuKey);
                if (prk != null && puk != null) {
                    System.out.println("nonull");
                    try {
                        ArrayList<String> parts = separeinpars(fromwallet);
                        String EWallet1 = cf.encrypt(parts.get(0), prk);
                        String EWallet2 = cf.encrypt(parts.get(1), prk);
                        String EWallet3 = cf.encrypt(parts.get(2), prk);
                        String EWallet4 = cf.encrypt(parts.get(3), prk);
                        ArrayList<String> ConEWallet = new ArrayList<>();
                        ConEWallet.add(EWallet1);
                        ConEWallet.add(EWallet2);
                        ConEWallet.add(EWallet3);
                        ConEWallet.add(EWallet4);

                        ArrayList<String> parts2 = separeinpars(towallet);
                        String EtoWallet1 = cf.encrypt(parts2.get(0), prk);
                        String EtoWallet2 = cf.encrypt(parts2.get(1), prk);
                        String EtoWallet3 = cf.encrypt(parts2.get(2), prk);
                        String EtoWallet4 = cf.encrypt(parts2.get(3), prk);
                        ArrayList<String> ConEtoWallet = new ArrayList<>();
                        ConEtoWallet.add(EtoWallet1);
                        ConEtoWallet.add(EtoWallet2);
                        ConEtoWallet.add(EtoWallet3);
                        ConEtoWallet.add(EtoWallet4);
                        System.out.println("r");
                        String ETondc = cf.encrypt(String.valueOf(tondc), prk);
                        vista.log.setText(vista.log.getText() + "\n" + "Iniciando transferencia");
                        DataOutputStream fs = new DataOutputStream(sc.getOutputStream());
                        fs.writeUTF("Transferencia" + "EOF");
                        ObjectOutputStream oos = new ObjectOutputStream(fs);
                        oos.writeObject(this.PuKey);
                        oos.writeObject(ConEWallet);
                        oos.writeObject(ConEtoWallet);
                        oos.writeObject(ETondc);
                        DataInputStream fa = new DataInputStream(sc.getInputStream());
                        String read = fa.readUTF();
                        vista.log.setText(vista.log.getText() + "\n" + read);
                    } catch (Exception ex) {

                        System.out.println("Error en cifrado");
                        vista.log.setText(vista.log.getText() + "\n" + "Error en cifrado");
//                        Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        activos = false;
                        try {
                            sc.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
}
