/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class Multiple {

    public static File RequestFile(String name, ArrayList<String> Nodes, File filee) {
        final int puerto = 9001;
        String server = Nodes.get(0);
        System.out.println("S=" + server);
        File file = null;
        try {
            Socket sc = new Socket(server, puerto);
            DataInputStream fa = new DataInputStream(sc.getInputStream());
            String read = fa.readUTF();
            System.out.println("read=" + read);
            DataOutputStream fs = new DataOutputStream(sc.getOutputStream());
            DataInputStream fi = new DataInputStream(sc.getInputStream());
            file = new File(name + "x.bin");
            FileOutputStream fos = new FileOutputStream(file);
            if (name == "Chain") {
                fs.writeUTF("GetChainList" + "EOF");
            } else {
                fs.writeUTF("GetPendingList" + "EOF");
            }
            long extend = fi.readLong();
            if (extend > 0) {
                byte[] byteArray = new byte[1024];
                byteArray = fi.readNBytes(1024);
                for (int n = 0; n < extend; n++) {
                    fos.write(byteArray);
                    byteArray = fi.readNBytes(1024);
                }
                fos.write(byteArray);
            }
            fos.close();
            sc.close();
            long tam = 0;
            if (filee.exists()) {
                tam = Files.size(filee.toPath());
                System.out.println("tam="+tam);
            }
            if (tam+1 > Files.size(file.toPath())) {
                    System.out.println("nop");
//                Files.copy(filee.toPath(), new File(name + ".bin").toPath());
                Files.delete(new File(name + "x.bin").toPath());
                file  = filee;
            } else {
                if (filee.exists()) {
                    filee.deleteOnExit();
//                    Files.delete(filee.toPath());
                }
                Files.copy(file.toPath(), new File(name + ".bin").toPath());
                Files.delete(new File(name + "x.bin").toPath());
                file  = new File(name + ".bin");
                System.out.println("ren_fin");
            }
            System.out.println(name + " Descargado");
        } catch (IOException ex) {
            Logger.getLogger(Multiple.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

}
