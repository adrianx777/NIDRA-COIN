/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import Pack1.Block;
import Pack1.Transactions;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class Multiple {

    public static File RequestFileFirstTime(String name, File filee,VistaServer vista) {
        vista.bar.setVisible(true);
        final int puerto = 9001;
        ArrayList<String> Nodes = readnodes();
        ArrayList<Object[]> lista = new ArrayList<>();
        File file = null;
        int i = 0;
        for (String server : Nodes) {
            i++;
            System.out.println("S=" + server);
            try {
                Socket sc = new Socket(server, puerto);
                sc.setSoTimeout(2000);
                DataInputStream fa = new DataInputStream(sc.getInputStream());
                String read = fa.readUTF();
                System.out.println("read=" + read);
                DataOutputStream fs = new DataOutputStream(sc.getOutputStream());
                DataInputStream fi = new DataInputStream(sc.getInputStream());
                file = new File(name + "x.bin" + i);
                FileOutputStream fos = new FileOutputStream(file);
                if (name.equals("Chain")) {
                    fs.writeUTF("GetChainList" + "EOF");
                } else {
                    fs.writeUTF("GetPendingList" + "EOF");
                }
                long extend = fi.readLong();
                if (extend > 0) {
                    byte[] byteArray = new byte[1024];
                    byteArray = fi.readNBytes(1024);
                    for (int n = 0; n < extend; n++) {
                        int x = (int)(n*100/extend);
                        vista.bar.setValue(x);
                        fos.write(byteArray);
                        byteArray = fi.readNBytes(1024);
                    }
                    fos.write(byteArray);
                }
                vista.bar.setValue(100);
                fos.close();
                sc.close();
                System.out.println(name + " Descargado");

            } catch (IOException ex) {
                    System.out.println("servidor no disponible:"+server);
//                Logger.getLogger(Multiple.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (file != null) {
                //////////////////
                String hash = null;
                try {
                    byte[] b = Files.readAllBytes(Paths.get(file.getPath()));
                    byte[] hashx = MessageDigest.getInstance("MD5").digest(b);
                    hash = bytesToHex(hashx);
                } catch (IOException ex) {
                    Logger.getLogger(Multiple.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Multiple.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (hash != null) {
//                    String hash = bytesToHex(digest);
                    Object[] objects = new Object[]{file, hash};
                    lista.add(objects);
                }
            }
        }
        ArrayList<Object[]> max = new ArrayList<>();
        for (Object[] serv : lista) {
            String myhash = (String) serv[1];
            File myFile = (File) serv[0];
            Object[] objects = new Object[]{myFile, 0};
            max.add(objects);
            for (Object[] oserv : lista) {
                String hash = (String) oserv[1];
                if (myhash.equals(hash) && serv != oserv && validatechainfile((File) serv[0])) {
                    objects[1] = (int) objects[1] + 1;
                    System.out.println("xxx");
                }
            }
        }
        Object[] maxx = new Object[]{null, 0};
        for (Object[] obj : max) {
            if ((int) obj[1] >= (int) maxx[1]) {
                maxx[0] = obj[0];
            }
        }
        file = (File) maxx[0];
        for (Object[] serv : max) {
            if ((File) serv[0] != file) {
                try {
                    Files.delete(((File) serv[0]).toPath());
                } catch (Exception ex) {
                }
            }
        }
        try {
            Files.deleteIfExists(new File(name+".bin").toPath());
        } catch (Exception ex) {
        }
        try {
            Files.move(file.toPath(), new File(name+".bin").toPath());
            file = new File(name+".bin");
        } catch (IOException ex) {
            System.out.println("Copy Mode");
            try {
                Files.copy(file.toPath(), new File(name+".bin").toPath());
                file = new File(name+".bin");
//            Logger.getLogger(Multiple.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex1) {
                System.out.println("Copy Failed");
            }
        }
        System.out.println("FILE=" + file.getPath());
        vista.bar.setVisible(false);
        return file;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean validatehash(Block block, Block lastblock) {
        if (block.calchash().equals(block.getHash()) && lastblock.getHash().equals(block.getPreviousHash())) {
            if (block.getTransactions() != null && ValidReward(block)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean ValidReward(Block b){
        for(Transactions t: b.getTransactions()){
            if(t.getFromAddress().equals("") && t.getAmount()!=1){
                return false;
            }
        }
        return true;
    }

    private static boolean validatechainfile(File file) {
        if(file.toPath().toString().contains("Pending")){
            System.out.println("is pending file");
            return true;
        }
        System.out.println("FILEx=" + file.getPath());
        BufferedReader bufferreader = null;
        boolean valido = true;
        if (file.length() == 0) {
            System.out.println("FILEZERO");
            BlockChain.Writegenesis(file);
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois;
            Block b = null;
            Block lastblock = null;
            ois = new ObjectInputStream(fis);
            do {
                b = (Block) ois.readObject();
                if (b != null && lastblock != null) {
                    boolean valid = validatehash(b, lastblock);
                    if (!valid) {
                        System.out.println("no valido");
                        valido = false;
                        break;
                    }
                }
                if (lastblock == null) {
                    lastblock = b;
                }
                lastblock = b;
            } while (b != null);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("fin del archivo");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bufferreader != null) {
                    bufferreader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valido;
    }

    public static ArrayList<String> readnodes() {
        ArrayList<String> Nodes = new ArrayList<>();
        File file = new File("Nodes.txt");
        if (file.exists()) {
            BufferedReader bufferreader = null;
            try {
                bufferreader = new BufferedReader(new FileReader(file));
                String line = bufferreader.readLine();
                do {
                    System.out.println("line=" + line);
                    Nodes.add(line);
                    line = bufferreader.readLine();
                } while (line != null);
                return Nodes;
            } catch (FileNotFoundException ex) {
                System.out.println("Archivo no encontrado Nodes");
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                System.out.println("Error IO");
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    bufferreader.close();
                } catch (IOException ex) {
                    System.out.println("Error IO");
//                    Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
    
    public static String genbarra(int n,int max){
        String s = "";
        String e = "";
        for(int i=0;i<max;i++){
        e = e+" ";
        }
        for(int i=0;i<n;i++){
        s = s+"=";
        }
        String x = e;
        x = e.substring(n, max);
        String nr = "["+s+x+"]\r";
        return nr;
    }

}
