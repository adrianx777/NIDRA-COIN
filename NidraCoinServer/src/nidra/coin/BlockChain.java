/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import Pack1.Block;
import Pack1.Transactions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class BlockChain {

    ArrayList<Block> chain = new ArrayList<>();
    int diff = 5;
    ArrayList<Transactions> pending = new ArrayList<>();
    int reward = 1;
    int lastSave = 0;
    int lastSavep = 0;

    public ArrayList<Block> getChain() {
        return chain;
    }

    public BlockChain() {
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                File file = new File("Chain.bin");
//                    if (!file.exists()) {
                        file = Multiple.RequestFileFirstTime("Chain", file);
                        if (file != null) {
                            try {
                                FileInputStream fis = new FileInputStream(file);
                                ObjectInputStream ois = new ObjectInputStream(fis);
                                Block b = (Block) ois.readObject();
                                if (b == null) {
                                    Writegenesis(file);
                                }

                            } catch (FileNotFoundException ex) {
                                System.out.println("Archivo no encontrado 1");
//                        Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                System.out.println("Chain vacia");
                                Writegenesis(file);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            System.out.println("chainfileisnull");
                        }
                        try {
                            BufferedReader bufferreader = new BufferedReader(new FileReader(file));
                            String line = bufferreader.readLine();
                        } catch (FileNotFoundException ex) {
                            System.out.println("Archivo no encontrado");
//                    Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            System.out.println("Error IO");
//                    Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
                        }
//                    }
                        File file2 = new File("Pending.bin");
//                    if (!file2.exists()) {
                        Multiple.RequestFileFirstTime("Pending", file2);
//                    }
                    generarchaintxt(true);
                    generarpendingtxt(true);
//                    Multiple.ConnectToRed();
            }
        };
        thread2.start();

    }

    public int getDiff() {
        return diff;
    }

    public ArrayList<Transactions> getPending() {
        return pending;
    }

    private void updatecbin() {
        FileOutputStream ous = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File("Chain.bin");
            for (int i = lastSave; i < chain.size(); i++) {
                ous = new FileOutputStream(file, true);
                oos = new ObjectOutputStream(ous);
                oos.writeObject(chain.get(i));
            }
            lastSave = chain.size() - 0;
//            System.out.println("LASTS=" + lastSave);
            if (oos != null) {
                oos.flush();
                oos.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error IO");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updatependingbin(boolean Nooverw) {
        FileOutputStream ous = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File("Pending.bin");
            for (int i = 0; i < pending.size(); i++) {
                ous = new FileOutputStream(file, false);
                oos = new ObjectOutputStream(ous);
                oos.writeObject(pending.get(i));
            }
            lastSavep = pending.size() - 1;
//            System.out.println("LASTS=" + lastSave);
//            oos.flush();
            if (oos != null) {
                oos.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error IO");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generarpendingtxt(boolean addtopending) {
        File file = new File("Pending.bin");
        System.out.println("Generando Pending.txt");
        File filetxt = new File("Pending.txt");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filetxt));
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois;
            Transactions t = null;
            do {
                ois = new ObjectInputStream(fis);
                t = (Transactions) ois.readObject();
                if (t != null) {
                    if (addtopending == true) {
//                        System.out.println("added="+b.toString());
                        pending.add(t);
                        lastSave++;
                    }
                    bw.write(t.toString());
                    bw.write("\n");
                }
            } while (t != null);
            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("fin del archivo");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                System.out.println("Error IO");
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void generarchaintxt(boolean addtochain) {
        File file = new File("Chain.bin");
        System.out.println("Generando Chain.txt");
        File filetxt = new File("Chain.txt");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filetxt));
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois;
            Block b = null;
            do {
                ois = new ObjectInputStream(fis);
                b = (Block) ois.readObject();
                if (b != null) {
                    if (addtochain == true) {
                        System.out.println("added=" + b.toString());
                        chain.add(b);
                        lastSave++;
                    }
                    bw.write(b.toString());
                    bw.write("\n");
                }
            } while (b != null);
            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("fin del archivo");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                System.out.println("Error IO");
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void Writegenesis(File file) {
        FileOutputStream ous = null;
        try {
            ous = new FileOutputStream(file, true);
            ObjectOutputStream oos = new ObjectOutputStream(ous);
            oos.writeObject(createGenesisBlock());
            oos.flush();
            oos.close();
            System.out.println("Write genesis");
//            lastSave = 0;
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado Genesis");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error IO");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ous.close();
            } catch (IOException ex) {
                System.out.println("Error IO");
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Block createGenesisBlock() {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        ArrayList<Transactions> trans = new ArrayList<>();
        trans.add(new Transactions("", "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIVm4lSY8jAxo5M8IvkKWxRBgMSSejmMFnMv7Eqd2oHUFJtI1DVpY0q2JBP779CDeBtwAJ4somijzOXMhwJEDZUCAwEAAQ==", 650000));
        Block b = new Block(timestamp, trans, "000000000000000000");
        return b;
    }

    public Block getlastblock() {
            return this.chain.get(this.chain.size() - 1);
    }

//    public void minePendingTransactions(String address) {
//        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//        Block block = new Block(timestamp, this.pending, null);
//        block.setPreviousHash(this.getlastblock().getHash());
//        block.Mine(diff, getlastblock());
//        System.out.println("valido=" + validatehash(block));
//        pushblock(block);
//        System.out.println("BLOQUE MINADO!");
//        updatecbin();
////        pending = new ArrayList<>();
//        for (Transactions t : block.getTransactions()) {
//            this.pending.remove(t);
//        }
//        Transactions rewardtrans = new Transactions("", address, this.reward);
//        this.pending.add(rewardtrans);
//        this.updatependingbin(false);
//        this.updatependingtxt(rewardtrans, false);
//    }
    public void minedblockget(Block block, String address) {
        boolean valid = validatehash(block);
        if (valid == true) {
            pushblock(block);
            System.out.println("BLOQUE MINADO!");
            updatecbin();
//            ArrayList<Transactions> pendingx = new ArrayList<>();
            System.out.println("trans=" + block.getTransactions().toString());
            try {
                for (Transactions t : block.getTransactions()) {
                    for (Transactions tp : pending) {
                        if (tp.getFromAddress().equals(t.getFromAddress()) && tp.getToAddress().equals(t.getToAddress()) && tp.getAmount() == t.getAmount()) {
                            pending.remove(tp);
                        }

                    }
                }
            } catch (Exception ex) {

            }
            System.out.println("pending=" + pending.toString());
//            pending = new ArrayList<>();
            Transactions rewardtrans = new Transactions("", address, this.reward);
            this.pending.add(rewardtrans);
            this.updatependingbin(false);
            this.updatependingtxt(rewardtrans, false);
        } else {
            System.out.println("Bloque invalido");
        }
    }

    public boolean validatehash(Block block) {
        if (block.calchash().equals(block.getHash()) && getlastblock().getHash().equals(block.getPreviousHash())) {
            if (block.getTransactions() != null) {
                return true;
            }
        }
        return false;
    }

    public void createTransaction(Transactions trans) {
        this.pending.add(trans);
        this.updatependingbin(true);
        this.updatependingtxt(trans, true);
    }

    public void updatependingtxt(Transactions trans, boolean Nooverw) {
        FileWriter fw = null;
        try {
            File file = new File("Pending.txt");
            fw = new FileWriter(file.getAbsoluteFile(), Nooverw);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(trans.toString());
            bw.write("\n");
            bw.close();
        } catch (IOException ex) {
            System.out.println("Error en escritura");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void pushblock(Block block) {
        FileWriter fw = null;
        this.chain.add(block);
        try {
            File file = new File("Chain.txt");
            fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(block.toString());
            bw.write("\n");
            bw.close();
        } catch (IOException ex) {
            System.out.println("Error en lectura");
//            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                System.out.println("Error en cierre lectura");
//                Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public double getBalanceofAddress(String address) {
        double nr = 0;
        try {
            for (Block b : this.chain) {
                for (Transactions t : b.getTransactions()) {
//                System.out.println("t=" + t.toString());
                    if (t.getFromAddress().equals(address)) {
                        nr -= t.getAmount();
                    }
                    if (t.getToAddress().equals(address)) {
                        nr += t.getAmount();
                    }
                }
            }
            for (Transactions t : this.pending) {
                if (t.getFromAddress().equals(address)) {
                    nr -= t.getAmount();
                }
//                if (t.getToAddress().equals(address)) {
//                    nr += t.getAmount();
//                }
            }
        } catch (Exception ex) {

        }
        return nr;
    }

}
