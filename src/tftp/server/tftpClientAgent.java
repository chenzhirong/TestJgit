/*
 * tftpClientAgent.java
 *
 * Created on 2007年4月29日, 上午9:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tftp.server;

import java.net.*;
import java.io.*;

/**
 *
 * @author Administrator
 */
public class tftpClientAgent extends Thread {
    
    /** Creates a new instance of tftpClientAgent */

    public tftpClientAgent(InetAddress ip, int port, short opcode, String fname, String mode) {
        this.m_ClientAddress = ip;
        this.m_ClientPort = port;
        this.m_curopcode = opcode;
        this.m_filename = fname;
        this.m_mode = mode;
    }
    
    public void run() {
        int nfail = 100;
        while (nfail-- > 0) {
            //try getFreePort 100 times
            try {
                this.m_so_tftp = new DatagramSocket(this.getFreePort());
                break;  //get a random port number
            } catch (SocketException e) {
            }
        }
        
        //ok, the UDP socket is ready, response to the client
        switch (this.m_curopcode) {
            case 1:
                //RRQ
                this.RRQ();
                break;
            case 2:
                //WRQ
                this.WRQ();
                //System.out.println("debug: tftpClientAgent.run() --> a WRQ ended ...");
                break;
        }
        
        this.m_so_tftp.close();
        
    }
    
    public void RRQ() {
        int ntimeout = this.m_MAX_nTimeOut;
        try {
            short nblock = 1;
            //send the #0 block ACK to start the transfer
            if (!this.SendFile(nblock, this.m_filename, this.m_mode)) {
                this.SendERROR((short)0, "无法读取文件");
                return ;
            }
            
            //wait for the ACK packet
            while (ntimeout > 0) {
                DatagramPacket dp;
                dp = this.waitForData();
                
                if (dp == null) {
                    //this.SendERROR((short)0, "超时了，连接被服务器断开 ...");
                    //System.out.println("debug: tftpClientagent.RRQ() --> timeout: " + ntimeout);
                    ntimeout--;
                } else {
                    //ok, get a packet, check the ip and port
                    if (!((dp.getAddress().equals(this.m_ClientAddress))
                            && (dp.getPort() == this.m_ClientPort))) {
                        //ip or port has a mistake
                        //this.SendERROR((short)0, "我不认识你");
                        //System.out.println("debug: RRQ() --> ip or port error ...");
                        ntimeout--;
                    } else {    //right ip and port
                        //get the opcode
                        DataInputStream din = new DataInputStream(new ByteArrayInputStream(dp.getData()));
                        int opcode = din.readShort();
                        //System.out.println("debug: the opcode is " + opcode);
                        if (opcode != 4) {
                            //不是04号ACK包
                            if (opcode == 1 && nblock == 1) {
                                //是01号RRQ包，且正在等待#1号ACK包
                                //此时收到RRQ，说明#1号data有可能失败了，重发
                                if (!this.SendFile(nblock, this.m_filename, this.m_mode)) {
                                    this.SendERROR((short)0, "无法读取文件");
                                    return ;
                                }
                            } else {
                                this.SendERROR((short)4, "非法的TFTP操作");
                            }
                            
                            ntimeout--;
                        } else {    //is an ACK packet
                            //System.out.println("debug: get a ACK packet");
                            int nblk = din.readShort();
                            //System.out.println("debug: the waiting for #" + nblock + " -- the curBlock is #" + nblk);
                            if (nblk != nblock) {
                                //System.out.println("debug: re-SendFile(" + nblock + ")");
                                //不是期待的块号，重发data包，超时计数减1
                                if (!this.SendFile(nblock, this.m_filename, this.m_mode)) {
                                    this.SendERROR((short)0, "无法读取文件");
                                    return ;
                                }
                                ntimeout--;
                            } else {    //get the right ACK packet
                                //the rest of the special file
                                long restSize = this.getRestOfFile(nblock, this.m_filename);
                                //System.out.println("debug: the restSize = " + restSize);
                                if (restSize < 0) {
                                    //ok, 上次已经把剩余的发送完毕了,结束
                                    ntimeout = 0;
                                } else {
                                    //send the next block data
                                    nblock++;
                                    //System.out.println("debug: start sending ...");
                                    if (!this.SendFile(nblock, this.m_filename, this.m_mode)) {
                                        //System.out.println("debug: ERROR while sending the #" + nblock);
                                        this.SendERROR((short)0, "无法读取文件");
                                        ntimeout = 0;
                                    } else {
                                        //send succ
                                        //System.out.println("debug: send the #" + nblock);
                                        ntimeout = this.m_MAX_nTimeOut; //重置超时记数
                                        //continue wait for ACK of the current block
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("Exception in tftpClientAgent.RRQ() --> " + e.getMessage());
        }
    }
    
    public void WRQ() {
        int ntimeout = this.m_MAX_nTimeOut;
        try {
            short nblock = 0;
            //send the #0 block ACK to start the transfer
            this.SendACK(nblock++);
            
            //wait for the data packet
            while (ntimeout > 0) {
                DatagramPacket dp;
                dp = this.waitForData();
                //dp = new DatagramPacket(buf, 516);
                if (dp == null) {
                    //this.SendERROR((short)0, "超时了，连接被服务器断开 ...");
                    //System.out.println("debug: tftpClientagent.WRQ() --> timeout: " + ntimeout);
                    ntimeout--;
                } else {
                    //ok, get a packet, check the ip and port
                    if (!((dp.getAddress().equals(this.m_ClientAddress))
                            && (dp.getPort() == this.m_ClientPort))) {
                        //ip or port has a mistake
                        //this.SendERROR((short)0, "我不认识你");
                        //System.out.println("debug: ip or port error ...");
                        ntimeout--;
                    } else {    //right ip and port
                        //get the opcode
                        DataInputStream din = new DataInputStream(new ByteArrayInputStream(dp.getData()));
                        int opcode = din.readShort();
                        //System.out.println("debug: the opcode is " + opcode);
                        if (opcode != 3) {
                            //不是03号data包
                            if (opcode == 2 && nblock == 1) {
                                //是02号WRQ包，且正在等待#1号data包
                                //此时收到WRQ，说明#0号ACK有可能失败了，重发
                                this.SendACK((short)(nblock-1));
                            } else {
                                this.SendERROR((short)4, "非法的TFTP操作");
                            }
                            
                            ntimeout--;
                        } else {    //data packet
                            //System.out.println("debug: get a data packet");
                            int nblk = din.readShort();
                            //System.out.println("debug: the waiting for #" + nblock + " -- the curBlock is #" + nblk);
                            if (nblk != nblock) {
                                //System.out.println("debug: re-SendACK(" + (nblock-1) + ")");
                                //不是期待的块号，重发上一个包的ACK，超时计数减1
                                this.SendACK((short)(nblock-1));
                                ntimeout--;
                            } else {    //the right packet waiting for
                                //this.SaveFile(this.m_filename);
                                if (!this.SaveFile(nblock, dp, this.m_filename, this.m_mode)) {
                                    ntimeout = 0;
                                    this.SendERROR((short)3, "磁盘满或超过分配的配额");
                                } else {
                                    //send the current block ACK
                                    this.SendACK(nblock);
                                    //System.out.println("debug: dp.length = " + (dp.getLength()-4));
                                    if (dp.getLength()-4 >= 512) {
                                        //the transfer isn't ended
                                        nblock++;   //wait for the next block
                                        ntimeout = this.m_MAX_nTimeOut; //重置超时记数
                                    } else {
                                        //ok, there is no more data, the transfer succ
                                        ntimeout = 1;
                                        //continue wait for current block for 1 timeout,
                                        //in case of the last ACK's failing
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("Exception in tftpClientAgent.WRQ() --> " + e.getLocalizedMessage());
        }
    }
    
    private InetAddress m_ClientAddress;    //ip of the client
    private int m_ClientPort;   //port of the client
    private DatagramSocket m_so_tftp;   //the socket object send or get message
    private short m_curopcode; //the current opcode( wrq/rrq )
    private String m_filename;
    private String m_mode;
    private final int m_MAX_nTimeOut = 5;
    
    //send an ACK packet with special block number
    private void SendACK(short nblock) {
        byte[] bufACK;
        ByteArrayOutputStream bout = new ByteArrayOutputStream(4);
        DataOutputStream dout = new DataOutputStream(bout);
        try {
            dout.writeShort(4); //opcode
            dout.writeShort(nblock);    //block number
            bufACK = bout.toByteArray();
            //construct a UDP packet 
            DatagramPacket dpACK = new DatagramPacket(bufACK, 4, this.m_ClientAddress, this.m_ClientPort);
            //construct a UDP packet and send it
            this.m_so_tftp.send(dpACK);
        } catch (IOException ex) {
        }
    }
    
    //send an Error packet with special ErrCode and ErrMsg
    private void SendERROR(short ErrCode, String ErrMsg) {
        byte[] bufERROR;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        try {
            dout.writeShort(5); //opcode
            dout.writeShort(ErrCode);
            dout.write(ErrMsg.getBytes());
            dout.writeByte(0);  //end of the ErrMsg
            bufERROR = bout.toByteArray();
            //construct a UDP packet 
            DatagramPacket dpERROR = new DatagramPacket(bufERROR, bufERROR.length, this.m_ClientAddress, this.m_ClientPort);
            //construct a UDP packet and send it
            this.m_so_tftp.send(dpERROR);
        } catch (IOException ex) {
        }
    }

    //save the data into the special file
    private boolean SaveFile(int nblock, DatagramPacket dp, String fname, String mode) {
        //ignore the mode, use the binary mode will keep the data away from being changed
        boolean isSucc = false;
        if (nblock < 1) {
            return false;
        }
        
        File f = new File(fname);
        if (!f.exists()) {
            //file not exists
            try {
                f.createNewFile();  //create it
            } catch (IOException ex) {
                return false;
            }
        }

        RandomAccessFile rf;
        try {
            rf = new RandomAccessFile(f, "rw");
        } catch (FileNotFoundException ex) {
            return false;
        }
        
        byte[] buf = dp.getData();  //get the data buffer
        int buflen = dp.getLength();
        try {
            rf.seek((nblock-1)*512);    //move the file pointer to last write position
            rf.write(buf, 4, buflen-4); //write the data
            rf.setLength((nblock-1)*512 + buflen-4);    //reset the file size
            isSucc = true;
            rf.close();
        } catch (IOException ex) {
            return false;
        }
        return isSucc;
    }
    
    //send the special block data from the special file
    private boolean SendFile(int nblock, String fname, String mode) {
        boolean isSucc = true;
        File f = new File(fname);
        if (!f.exists()) {
            return false;   //file not exists
        } else {
            long rest = this.getRestOfFile(nblock-1, fname);
            //the rest data in this file
            if (rest < 0) {
                return true;    //ok, hava been finished
            } else {
                if (rest > 512) {
                    rest = 512; //in the tftp protocol, the max length of data is 512 Bytes
                }

                ByteArrayOutputStream bout = new ByteArrayOutputStream();   //the buffer
                DataOutputStream dout = new DataOutputStream(bout); //the buffer writer

                RandomAccessFile rf;
                try {
                    rf = new RandomAccessFile(f, "r");
                } catch (FileNotFoundException ex) {
                    return false;
                }
                try {
                    //construct a DATA packet
                    dout.writeShort(3); //the opcode: data
                    dout.writeShort(nblock);    //the block number
                    if (rest > 0) {
                        byte[] buf = new byte[(int)rest];
                        rf.seek((nblock-1)*512);
                        rf.read(buf);
                        rf.close();
                        dout.write(buf);    //the data read from the file
                    }
                    
                    //ok, consrtuct a UDP packet with the tftp Data packet
                    //and send it
                    DatagramPacket dp = new DatagramPacket(bout.toByteArray(), bout.size(), this.m_ClientAddress, this.m_ClientPort);
                    this.m_so_tftp.send(dp);
                } catch (IOException ex) {
                    return false;
                }
            }
        }
        
        //System.out.println("debug: SendFile() --> succ");

        return isSucc;
    }
    
    //get the rest of file, nblock have been transfered
    private long getRestOfFile(int nblock, String fname) {
        long restsize = 0;
        File f = new File(fname);
        restsize = f.length() - nblock*512;
        return restsize;
    }
    //wait for data packet
    private DatagramPacket waitForData() {
 //       int ntimeout = this.m_MAX_nTimeOut;
        int ntimeout = 1;
        DatagramPacket dp = null;
        
        //construct an UDP packet
        byte[] buf = new byte[516];
        this.initZeroByteArray(buf);
        dp = new DatagramPacket(buf, 516);

        while (ntimeout > 0) {
            try {
                this.m_so_tftp.setSoTimeout(1000);  //timeout: 1 second
                this.m_so_tftp.receive(dp);
                //System.out.println("waitForData() --> recieve succ ... data.length = " + dp.getLength());
                break;
            } catch (Exception e) {
                //any exception will be deal with a timeout
                ntimeout--;
                //System.out.println("waitForData() --> timeout: " + ntimeout);
            }  //timeout: 1 second
        }
        
        return (ntimeout>0 ? dp : null);
    }
    
    //get a port unusing
    private int getFreePort() {
        int nport;
        nport = 1024 + (int)(Math.random()*(6000-1024));
        return nport;
    }
    
    //init a byte[] with 0s
    private void initZeroByteArray(byte[] buf) {
        for (int i=0; i<buf.length; i++) {
            buf[i] = 0;
        }
    }

}
