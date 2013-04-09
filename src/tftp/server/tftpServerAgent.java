/*
 * tftpServerAgent.java
 *
 * Created on 2007年4月30日, 下午1:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tftp.server;

import java.net.*;
import java.io.*;
import java.util.*;


/**
 *
 * @author Administrator
 */
public class tftpServerAgent extends Thread {
    
    /** Creates a new instance of tftpServerAgent */
    public tftpServerAgent() {
        this.m_hashClients = new Hashtable();
    }


    private Hashtable m_hashClients;
    
    //over the parent's run methord
    public void run() {
        try {
            DatagramSocket tftpd = new DatagramSocket(69);  //tftp server socket
            byte[] buf = new byte[516]; //a buffer for UDP packet
            DatagramPacket dp = new DatagramPacket(buf, 516);   //a UDP packet

            DataInputStream din = null;
            tftpClientAgent newClient = null;
            short tftp_opcode = 0;  //opcode: the 2 bytes in the front of tftp packet
            String tftp_filename = null;
            String tftp_mode = null;
            while (true) {
                tftpd.receive(dp);  //wait for a client
                buf = dp.getData(); //get the UDP packet data
                din = new DataInputStream(new ByteArrayInputStream(buf));
                tftp_opcode = din.readShort();  //get the opcode
                {   //get the filename
                    int fnoffset = 2;
                    int fnlen = 0;
                    while (din.readByte() != 0) {
                        fnlen++; //filename will end with a null char('\0')
                    }
                    tftp_filename = new String(buf, fnoffset, fnlen);
                    String downloadDir="D:/cfrManage/download";
                    File dir=new File(downloadDir);
                    if(!dir.exists()){
                    	dir.mkdirs();
                    }
                    tftp_filename=downloadDir+"/"+tftp_filename;
                    //get the mode
                    int mdnoffset = fnoffset + fnlen + 2;
                    int mdnlen = 0;
                    while (din.readByte() != 0) {
                        mdnlen++; //filename will end with a null char('\0')
                    }
                    tftp_mode = new String(buf, mdnoffset, mdnlen);
                }

                switch (tftp_opcode) {
                    case 1:
                         //RRQ
                   case 2:
                        //WRQ
                        newClient = new tftpClientAgent(dp.getAddress(), dp.getPort(), tftp_opcode, tftp_filename, tftp_mode);
                        newClient.setDaemon(true);
                        newClient.start();
                                                
                        this.m_hashClients.put(dp.getAddress().toString()+dp.getPort(), newClient);
                        //System.out.println("debug: Main.main() --> a RRQ thread start ....");
                        break;
                }
            }
        } catch (Exception e) {
            
        }
    }

}
