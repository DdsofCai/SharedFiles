package CurriculumDesign.Client;

import javafx.stage.FileChooser;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5FileSend extends Thread {

    public void run() {

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择你要查看的文件");
            File f = fileChooser.showOpenDialog(null);

            Socket s = null;
            s = new Socket("localhost", 4567);
            //构建套接字输出流，以发送数据给服务器
            DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            s.getOutputStream()));

            //构建文件输入流
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(f)));

            //构建套接字输入流，接收服务器反馈信息
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            s.getInputStream()));
            long fileLen = f.length();  //计算文件长度
            //发送文件名称、文件长度
            out.writeUTF(f.getName());
            out.writeLong(fileLen);
            out.flush();
            //传送文件内容
            int numRead = 0; //单次读取的字节数
            int numFinished = 0; //总完成字节数
            byte[] buffer = new byte[8096];
            while (numFinished < fileLen && (numRead = in.read(buffer)) != -1) { //文件可读
                out.write(buffer, 0, numRead);  //发送
                out.flush();
                numFinished += numRead; //已完成字节数
            }//end while
            in.close();

            String response = br.readLine();//读取返回串
            if (response.equalsIgnoreCase("M_DONE")) { //服务器成功接收
                JOptionPane.showMessageDialog(null, "传送成功", "提示", JOptionPane.PLAIN_MESSAGE);
            } else if (response.equalsIgnoreCase("M_LOST")) { //服务器接收失败
                JOptionPane.showMessageDialog(null, "传送失败", "提示", JOptionPane.PLAIN_MESSAGE);
            }//end if
            //关闭流
            br.close();
            out.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
