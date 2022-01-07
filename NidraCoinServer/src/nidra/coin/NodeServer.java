/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import Pack1.Block;
import Pack1.Cifrado;
import Pack1.Transactions;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class NodeServer {

    ServerSocket skServidor;
    ServerSocket skServidorWallet;
    final int puerto = 9001;
    final int puertoW = 9002;
    ArrayList<Socket> AlmacenSocket;
    ArrayList<Socket> SocketWaiting;
    
     ArrayList<Socket> AlmacenSocketW;
    ArrayList<Socket> SocketWaitingW;
    String NodeName = "CentralNode";
    String lastmensaje = "No messages";

    BlockChain bc;

    public NodeServer(BlockChain bc) {
        System.out.println("startserver");
        this.bc = bc;
        AlmacenSocket = new ArrayList<>();
        SocketWaiting = new ArrayList<>();
        AlmacenSocketW = new ArrayList<>();
        SocketWaitingW = new ArrayList<>();
        try {
            skServidor = new ServerSocket(puerto);
            skServidorWallet = new ServerSocket(puertoW);
        } catch (IOException ex) {
            Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                serverAccept();
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                AceptarPeticiones();
                
            }
        };
        thread1.start();
        thread2.start();
        Thread thread3 = new Thread() {
            @Override
            public void run() {
                serverAcceptWallet();
            }
        };
        Thread thread4 = new Thread() {
            @Override
            public void run() {
                AceptarPeticionesWallet();
            }
        };
        thread3.start();
        thread4.start();
    }

    private void serverAccept() {
        while (true) {
            try {
                Socket sCliente = skServidor.accept();
                AlmacenSocket.add(sCliente);
                DataOutputStream fs = new DataOutputStream(sCliente.getOutputStream());
                fs.writeUTF("Conexion Correcta al Nodo (" + NodeName + ")");
                System.out.println("cliente conectado");
            } catch (IOException ex) {
                Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void serverAcceptWallet() {
        while (true) {
            try {
                Socket sClientew = this.skServidorWallet.accept();
                AlmacenSocketW.add(sClientew);
                DataOutputStream fs = new DataOutputStream(sClientew.getOutputStream());
                fs.writeUTF("Conexion Correcta al Nodo (" + NodeName + ")");
                System.out.println("cliente conectado");
            } catch (IOException ex) {
                Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized boolean isinwaiting(Socket s) {
        try {
            for (Socket sx : SocketWaiting) {
                if (sx == s) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("error=" + ex);
        }
//        System.out.println("faklse");
        return false;
    }

    private synchronized void removes(Socket s) {
        SocketWaiting.remove(s);
    }

    private synchronized void adds(Socket s) {
        SocketWaiting.add(s);
    }
    
    private synchronized boolean isinwaitingw(Socket s) {
        try {
            for (Socket sx : SocketWaitingW) {
                if (sx == s) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("error=" + ex);
        }
//        System.out.println("faklse");
        return false;
    }

    private synchronized void removesw(Socket s) {
        SocketWaitingW.remove(s);
    }

    private synchronized void addsw(Socket s) {
        SocketWaitingW.add(s);
    }

    private void Send(String name, Socket s) {
        DataOutputStream fs = null;
        try {
            fs = new DataOutputStream(s.getOutputStream());
            File file = new File(name);
            if (!file.exists()){
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            Long extend = Long.valueOf(fis.available() / 1024);
            byte[] byteArray = new byte[1024];
            fs.writeLong(extend);
            while (fis.read(byteArray) != -1) {
                fs.write(byteArray);
            }
            System.out.println("enviado finalizado");
        } catch (IOException ex) {
            Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fs.close();
            } catch (IOException ex) {
                Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void AceptarPeticiones() {
        while (true) {
            int con = 0;
            try {
                Thread.sleep(5);
                for (Socket s : AlmacenSocket) {
//                            System.out.println("sx="+AlmacenSocket.size());
                    con++;
                    if (s.isClosed()) {
                        AlmacenSocket.remove(s);
                        System.out.println("CLOSE");
                    } else if (isinwaiting(s) == false) {
                        adds(s);
                        Thread thread1 = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    DataInputStream fa = new DataInputStream(s.getInputStream());
//                                    long inicio = System.nanoTime();
                                    String read = fa.readUTF();
                                    System.out.println("read=" + read);
//                                    long fin = System.nanoTime();
                                    String[] parts = read.split("EOF");
                                    read = parts[0];
                                    if (read.contains("GetChainList")) {
                                        Send("Chain.bin", s);
                                    } else if (read.contains("GetPendingList")) {
                                        Send("Pending.bin", s);
                                    } else if (read.contains("GetBlock")) {
                                        DataOutputStream fs = new DataOutputStream(s.getOutputStream());
                                        ObjectOutputStream oos = new ObjectOutputStream(fs);
                                        Block lastb = bc.getlastblock();
                                        ArrayList<Object> obj = new ArrayList<>();
                                        obj.add(lastb);
                                        obj.add(bc.getPending());
                                        obj.add(bc.getDiff());
                                        oos.writeObject(obj);
                                        oos.writeObject(lastmensaje);
                                        oos.flush();
                                    } else if (read.contains("MinedBlock")) {
                                        DataInputStream is = new DataInputStream(s.getInputStream());
                                        ObjectInputStream ois = new ObjectInputStream(is);
                                        Block block = (Block) ois.readObject();
                                        String address = (String) ois.readObject();
                                        if (bc.validatehash(block) == true) {
//                                            ServerEnviar(address+" Mined: "+block.getHash());
                                                lastmensaje = address+" Mined: "+block.getHash();
                                        }
                                        bc.minedblockget(block, address);
                                    } else if (read.contains("GetBalance")) {
                                        String wallet = fa.readUTF();
                                        double balance = bc.getBalanceofAddress(wallet);
                                        DataOutputStream fs = new DataOutputStream(s.getOutputStream());
                                        fs.writeDouble(balance);
                                        fs.flush();
                                    }
                                    removes(s);
                                } catch (IOException ex) {
                                    System.out.println("conexion terminada");
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        };
                        thread1.start();
                    }
                }
            } catch (Exception ex) {
                con--;
                System.out.println("cliente desconectado");
            }
        }
    }
    
    private void AceptarPeticionesWallet() {
        while (true) {
            int con = 0;
            try {
                Thread.sleep(5);
                for (Socket s : AlmacenSocketW) {
//                            System.out.println("sx="+AlmacenSocket.size());
                    con++;
                    if (s.isClosed()) {
                        AlmacenSocketW.remove(s);
                        System.out.println("CLOSE");
                    } else if (isinwaitingw(s) == false) {
                        addsw(s);
                        Thread thread1 = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    DataInputStream fa = new DataInputStream(s.getInputStream());
//                                    long inicio = System.nanoTime();
                                    String read = fa.readUTF();
                                    System.out.println("read=" + read);
//                                    long fin = System.nanoTime();
                                    String[] parts = read.split("EOF");
                                    read = parts[0];
                                    if (read.contains("GetBalance")) {
                                        String wallet = fa.readUTF();
                                        double balance = bc.getBalanceofAddress(wallet);
                                        DataOutputStream fs = new DataOutputStream(s.getOutputStream());
                                        fs.writeDouble(balance);
                                        fs.flush();
                                    } else if (read.contains("Transferencia")) {
                                        DataOutputStream fs = new DataOutputStream(s.getOutputStream());
                                        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                                        Cifrado cf = new Cifrado();
                                        String Spukey = (String)ois.readObject();
                                        ArrayList<String> Ewallet0 = (ArrayList<String>)ois.readObject();
                                        ArrayList<String> Etowallet0 = (ArrayList<String>)ois.readObject();
                                        String Endc = (String)ois.readObject();
                                        try {
                                            PublicKey pukey = cf.Publickeygen(Spukey);
                                            String wallet01 = cf.decrypt(Ewallet0.get(0),pukey);
                                            String wallet02 = cf.decrypt(Ewallet0.get(1),pukey);
                                            String wallet03 = cf.decrypt(Ewallet0.get(2),pukey);
                                            String wallet04 = cf.decrypt(Ewallet0.get(3),pukey);
                                            String wallet = wallet01+wallet02+wallet03+wallet04;
                                            
                                            String towallet01 = cf.decrypt(Etowallet0.get(0),pukey);
                                            String towallet02 = cf.decrypt(Etowallet0.get(1),pukey);
                                            String towallet03 = cf.decrypt(Etowallet0.get(2),pukey);
                                            String towallet04 = cf.decrypt(Etowallet0.get(3),pukey);
                                            String towallet = towallet01+towallet02+towallet03+towallet04;
                                       
//                                            String wallet = cf.decrypt(Ewallet, pukey);
//                                            String toWallet = cf.decrypt(Etowallet, pukey);
                                            String ndc = cf.decrypt(Endc, pukey);
                                            double Tondc = Double.valueOf(ndc);
                                            double balance = bc.getBalanceofAddress(wallet);
                                            if (balance>Tondc){
                                            bc.createTransaction(new Transactions(wallet,towallet,Tondc));
                                            fs.writeUTF("Transaccion en marcha");
                                            }
                                        } catch (Exception ex) {
                                            fs.writeUTF("Error en la transaccion");
                                            System.out.println("error en la transaccion");
                                            Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        
                                    }
                                    removesw(s);
                                } catch (IOException ex) {
                                    System.out.println("conexion terminada");
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        };
                        thread1.start();
                    }
                }
            } catch (Exception ex) {
                con--;
                System.out.println("cliente desconectado");
            }
        }
    }

    private void ServerEnviar(String mensaje) {
        for (Socket s : AlmacenSocket) {
            if (s.isClosed()) {
                AlmacenSocket.remove(s);
            } else {
                try {
                    DataOutputStream fs = new DataOutputStream(s.getOutputStream());
                    fs.writeUTF(mensaje);
                } catch (IOException ex) {
//                    System.out.println("fin trans");
//                    Logger.getLogger(NodeServer.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

}
