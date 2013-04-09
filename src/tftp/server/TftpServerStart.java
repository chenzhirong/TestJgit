/*
 * Main.java
 *
 * Created on 2007年4月29日, 上午9:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tftp.server;

import java.net.*;
import java.io.*;

/**
 * @author Administrator
 */
public class TftpServerStart {
    /** Creates a new instance of Main */
    public static void StartServer() {
    	 tftpServerAgent tftp_srv = new tftpServerAgent();
         tftp_srv.setDaemon(true);
         tftp_srv.start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
       
//        byte[] buf = new byte[10];
//        byte b;
//        while (true) {
//            System.out.print(System.in.read());
//        }

    }
    
    //show the administrator's menu
    public void ShowMenu() {
        //String menu[] = new String[];
    }
}
