package CurriculumDesign.Client03;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Chess5FileRec extends Thread {
    ServerSocket socket=new ServerSocket(0);

    public Chess5FileRec() throws IOException {
    }

//    public void init(){
//        try {
//            socket = new ServerSocket(9999);
//            System.out.println(socket.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void run(){
        try {

//            System.out.println(socket.toString());
            while (true){
                Socket s = socket.accept();
                System.out.println(s.toString());

                //构建套接字输入流，接收客户端数据
                DataInputStream in=new DataInputStream(
                        new BufferedInputStream(
                                s.getInputStream()));
                //构建套接字输出流，以发送数据给客户端
                BufferedWriter out=new BufferedWriter(
                        new OutputStreamWriter(
                                s.getOutputStream()));

                //接收文件名、文件长度
                String filename=in.readUTF(); //文件名
                int fileLen=(int)in.readLong(); //文件长度
                //创建文件输出流
                File f =new File(new Date().getTime() + "." + filename);
                System.out.println(new Date().getTime() + "." + filename);
                DataOutputStream dos =new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(f)));

                byte buffer[] = new byte[8096];
                int numRead = 0;
                int numFinished = 0;
                while (numFinished<fileLen && (numRead = in.read(buffer))!=-1){
                    dos.write(buffer,0, numRead);
                    dos.flush();
                    numFinished += numRead;
                }
                dos.close();

                if (numFinished>=fileLen) {//文件接收完成？
                    out.write("M_DONE"); //回送成功消息
                    System.out.println(filename +"  接收成功！");
//                    JOptionPane.showMessageDialog(null, "加入游戏成功", "提示", JOptionPane.PLAIN_MESSAGE);

                }else {
                    out.write("M_LOST"); //回送失败消息
                    System.out.println(filename +"  接收失败！") ;
                }//end if
                out.newLine();
                out.flush();
                out.close();
                s.close();
//                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String getServerSocket(){
        String addr=socket.getInetAddress().toString();
        int port=socket.getLocalPort();

        return addr+","+port;
    }





}
