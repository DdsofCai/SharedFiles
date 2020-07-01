package CurriculumDesign.Client03;

import CurriculumDesign.Server.Player;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class Chess5FileChoose extends Application {


    DatagramSocket client;
    DatagramPacket pout;
    Button btn_send, btn_share, btn_file, btn_fresh, btn_quit;
    TextField tf_msg;
    TextArea recv_msg;
    Player localPlayer;
    ComboBox<String> cBox;
    ListView<String> list;
    Chess5PaneThread paneThread;

    public void start(Stage primaryStage) throws SocketException {


//        client.send();
        //面板
        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();

        //组件

        btn_file = new Button("选择文件");


        //布局
        gridPane.add(btn_file, 0, 0);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        stackPane.getChildren().add(gridPane);

        stackPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(stackPane, 100, 100);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess5 File Choose");


        primaryStage.show();
        btn_file.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                int file = Chess5ListenThread.getFile();
                if (file == 1) {
                    String[] content = Chess5ListenThread.getAddr().split(",");
                    String[] add = content[0].split("/");
                    try {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("选择你要查看的文件");
                        File f = fileChooser.showOpenDialog(null);

                        Socket s = null;
                        s = new Socket(add[0], Integer.parseInt(content[1]));
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
                            JOptionPane.showMessageDialog(null, "  传送成功！", "提示", JOptionPane.PLAIN_MESSAGE);
                        } else if (response.equalsIgnoreCase("M_LOST")) { //服务器接收失败
                            JOptionPane.showMessageDialog(null, "  传送失败！", "提示", JOptionPane.PLAIN_MESSAGE);

                        }//end if
                        //关闭流
                        br.close();
                        out.close();
                        s.close();
                        primaryStage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }
}





