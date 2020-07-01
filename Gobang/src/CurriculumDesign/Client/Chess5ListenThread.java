package CurriculumDesign.Client;

import CurriculumDesign.Server.Message;
import CurriculumDesign.Server.MessageType;
import CurriculumDesign.Server.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5ListenThread extends Thread {
    private static int file;
    DatagramSocket client = null;
    Chess5PaneThread paneThread = null;
    ObservableList options = null;
    Player localPlayer;
    Chess5Com com;
    int[][] data = new int[10][10];
    private static String addr;

    public Chess5ListenThread(DatagramSocket client, Chess5PaneThread paneThread, Chess5Com com) {
        this.client = client;
        this.paneThread = paneThread;
        this.com = com;

    }

    public static String printArr1(int[][] arr) {
        String str = "";
        for (int x = 0; x < arr.length; x++) {
            for (int y = 0; y < arr[x].length; y++) {
                str = str + arr[x][y];
            }
        }
        return str;
    }

    public static int getFile() {
        return file;
    }

    public static String getAddr() {
        return addr;
    }

    public void run() {
        while (true) {
            try {
                DatagramPacket pin = new DatagramPacket(new byte[1024], 1024);
                client.receive(pin);
                Message msg = (Message) Message.convertToObj(pin.getData(), 0, pin.getLength());
                Message rspMsg = new Message();
                DatagramPacket pout = null;

                if (msg.getMsgType() == MessageType.JOIN_GAME) {
                    int result = msg.getStatus();
                    if (result == 1) {
                        //加入游戏成功
//                        paneThread.btn_join_game.setDisable(true);
                        localPlayer = msg.getToPlayer();
//                        JOptionPane.showMessageDialog(null, "加入游戏成功", "提示", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        //加入游戏失败
//                        paneThread.btn_join_game.setDisable(false);
                        JOptionPane.showMessageDialog(null, "该玩家已经登录", "提示", JOptionPane.PLAIN_MESSAGE);
                        System.exit(0);
                    }
                } else if (msg.getMsgType() == MessageType.JOIN_HOST) {
                    int result = msg.getStatus();
                    if (result == 1) {
                        //加入擂台成功
                        paneThread.btn_join_host.setDisable(true);
                        paneThread.btn_quit_host.setDisable(false);
                        paneThread.btn_update_host.setDisable(false);
                        paneThread.cBox.setDisable(false);
                        paneThread.btn_challenge.setDisable(false);
                        paneThread.btn_guanzhan.setDisable(false);
                        paneThread.rbtn_white.setDisable(false);
                        paneThread.rbtn_black.setDisable(false);
                        paneThread.btn_information.setDisable(false);
                        paneThread.btn_send_msg.setDisable(false);
                        paneThread.btn_clear.setDisable(true);
                        paneThread.btn_huiqi.setDisable(true);
                        paneThread.btn_dofall.setDisable(true);
                        paneThread.btn_surrend.setDisable(true);
                        paneThread.btn_save_pane.setDisable(true);
                        paneThread.btn_url.setDisable(false);
                        JOptionPane.showMessageDialog(null, "加入擂台成功", "提示", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        //加入擂台失败
                        paneThread.btn_join_host.setDisable(false);
                        JOptionPane.showMessageDialog(null, "加入擂台失败", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                } else if (msg.getMsgType() == MessageType.UPDATE_HOST) {
                    String recvMsg = msg.getContent().toString();
                    System.out.println(recvMsg);
                    options = FXCollections.observableArrayList();
                    String name[] = recvMsg.split(":");
                    for (int i = 0; i < name.length; i++) {
                        options.add(name[i]);
                    }
//                    paneThread.cBox.setItems(options);
                    paneThread.setcBox(options);
                } else if (msg.getMsgType() == MessageType.CHALLENGE) {
                    paneThread.reClear();
                    try {
                        Player challenge = msg.getFromPlayer();
                        String color = "";
                        if (challenge.getColor() == 1) {
                            localPlayer.setColor(2);
                            color = "黑子后手";
                        } else {
                            localPlayer.setColor(1);
                            color = "白子先手";
                        }
                        int n = JOptionPane.showConfirmDialog(null, challenge.getName() + "发起挑战，对方执行" + color, "收到挑战书", JOptionPane.YES_NO_OPTION);//1拒绝-0接受
                        //接受挑战
                        if (n == 0) {
                            rspMsg.setStatus(1);
                            if (localPlayer.getColor() == 2) {
                                paneThread.rbtn_white.setSelected(true);
                                paneThread.rbtn_black.setSelected(false);
                                paneThread.pane.setMouseTransparent(false);
                            } else {
                                paneThread.rbtn_white.setSelected(false);
                                paneThread.rbtn_black.setSelected(true);
                                paneThread.pane.setMouseTransparent(true);
                            }

                            paneThread.gameStart();

                        } else {
                            rspMsg.setStatus(0);
                        }
                        rspMsg.setMsgType(4);
                        rspMsg.setToPlayer(challenge);
                        rspMsg.setFromPlayer(localPlayer);
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                        client.send(pout);

                        Competition competition = new Competition();
                        competition.setLocalPlayer(localPlayer);
                        competition.setRemotePlayer(challenge);
                        paneThread.pane.setCompetition(competition);
                        paneThread.pane.setColor(localPlayer.getColor());
                        paneThread.pane.getCompetition().setResult(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (msg.getMsgType() == MessageType.CHALLENGE_RSP) {

                    int result = msg.getStatus();
                    if (result == 1) {


                        if (msg.getFromPlayer().getColor() == 1) {
                            localPlayer.setColor(2);
                        } else {
                            localPlayer.setColor(1);
                        }

                        Player remotePlayer = msg.getFromPlayer();
                        Competition competition = new Competition();
                        competition.setLocalPlayer(localPlayer);
                        competition.setRemotePlayer(remotePlayer);
                        paneThread.pane.setColor(localPlayer.getColor());
                        paneThread.pane.setCompetition(competition);
                        paneThread.pane.getCompetition().setResult(0);
                        paneThread.pane.clear();
                        if (localPlayer.getColor() == 2) {//1黑2白
                            paneThread.rbtn_white.setSelected(true);
                            paneThread.pane.setMouseTransparent(false);
                        } else {
                            paneThread.rbtn_black.setSelected(true);
                            paneThread.pane.setMouseTransparent(true);
                        }
                       paneThread.gameStart();

                    }else if(result==-1){
                        JOptionPane.showMessageDialog(null, "对方正在游戏中", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "对方不接受挑战", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                } else if (msg.getMsgType() == MessageType.PLAY) {
                    Player remotePlayer = msg.getFromPlayer();
                    Competition competition = new Competition();
                    competition.setLocalPlayer(localPlayer);
                    competition.setRemotePlayer(remotePlayer);
                    paneThread.pane.setCompetition(competition);
                    String content = msg.getContent().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                    String[] dataT = content.split(",");
                    int column = Integer.parseInt(dataT[0]);
                    int row = Integer.parseInt(dataT[1]);
                    int color = Integer.parseInt(dataT[2]);
                    int action = Integer.parseInt(dataT[3]);
                    paneThread.draw(row, column, color);//上面如果报错的话
                    paneThread.pane.setMouseTransparent(false);
                } else if (msg.getMsgType() == MessageType.SURRENDER) {
                    JOptionPane.showMessageDialog(null, msg.getFromPlayer().getName() + "发起投降！" + (localPlayer.getColor() == 1 ? "黑子" : "白子") + "胜", "提示", JOptionPane.PLAIN_MESSAGE);
                    paneThread.pane.setMouseTransparent(true);
                    paneThread.gameOver();
                } else if (msg.getMsgType() == MessageType.HUIQI) {
                    System.out.println(msg.getContent().toString());
                    int n = JOptionPane.showConfirmDialog(null, "接受对方的悔棋请求吗", "警告", JOptionPane.YES_NO_OPTION);//1拒绝-0接受
                    String paneData = msg.getContent().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                    String[] removePane = paneData.split(",");
                    paneThread.pane.removeChess(Integer.parseInt(removePane[0]), Integer.parseInt(removePane[1]), Integer.parseInt(removePane[2]));
                    paneThread.pane.setMouseTransparent(true);
                    Message sendMsg = new Message();
                    sendMsg.setMsgType(9);
                    if (n == 0) {
                        sendMsg.setStatus(1);
                        paneThread.gameOver();
                    } else {
                        sendMsg.setStatus(0);
                    }
                    sendMsg.setFromPlayer(localPlayer);
                    sendMsg.setToPlayer(msg.getFromPlayer());
                    byte[] data = Message.convertToBytes(sendMsg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);

                } else if (msg.getMsgType() == MessageType.HUIQI_RSP) {
                    int result = msg.getStatus();
                    if (result == 1) {
                        String paneData = msg.getContent().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                        String[] removePane = paneData.split(",");
                        paneThread.pane.removeChess(Integer.parseInt(removePane[0]), Integer.parseInt(removePane[1]), Integer.parseInt(removePane[2]));
                        paneThread.pane.setMouseTransparent(false);
                        System.out.println("接受悔棋");
                    } else {
                        System.out.println("不接受悔棋");
                    }
                } else if (msg.getMsgType() == MessageType.DOGFALL) {
                    int n = JOptionPane.showConfirmDialog(null, "接受对方的平局请求吗", "警告", JOptionPane.YES_NO_OPTION);//1拒绝-0接受
                    Message sendMsg = new Message();
                    sendMsg.setToPlayer(msg.getFromPlayer());
                    sendMsg.setFromPlayer(localPlayer);
                    if (n == 0) {
                        paneThread.pane.setMouseTransparent(true);
                        sendMsg.setStatus(1);
                        sendMsg.setMsgType(12);
                        paneThread.gameOver();
                    } else {
                        sendMsg.setStatus(0);
                    }
                    byte[] data = Message.convertToBytes(sendMsg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);

                } else if (msg.getMsgType() == MessageType.DOGFALL_RSP) {
                    int result = msg.getStatus();
                    if (result == 1) {
                        paneThread.pane.setMouseTransparent(true);
                        JOptionPane.showMessageDialog(null, "对方接受平局", "提示", JOptionPane.PLAIN_MESSAGE);
                        paneThread.gameOver();

                    } else {
                        JOptionPane.showMessageDialog(null, "对方拒绝平局", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                } else if (msg.getMsgType() == MessageType.UPDATE_COM) {
                    String recvMsg = msg.getContent().toString();
                    System.out.println(recvMsg);
                    options = FXCollections.observableArrayList();
                    String name[] = recvMsg.split(":");
                    for (int i = 0; i < name.length; i++) {
                        options.add(name[i]);
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            com.cBox.setItems(options);
                            com.list.setItems(options);
                        }
                    });

                } else if (msg.getMsgType() == MessageType.JOIN_COM) {
                    com.recv_msg.appendText("(公共信息)欢迎" + msg.getContent().toString() + "进入聊天室\r\n");
                } else if (msg.getMsgType() == MessageType.PUBLIC_CHAT) {
                    com.recv_msg.appendText("(公共信息)玩家" + msg.getFromPlayer().getName() + ":" + msg.getContent().toString() + "\r\n");
                } else if (msg.getMsgType() == MessageType.PRIVATE_CHAT) {
                    com.recv_msg.appendText("(私聊信息)玩家" + msg.getFromPlayer().getName() + ":" + msg.getContent().toString() + "\r\n");
                } else if (msg.getMsgType() == MessageType.QUIT_COM) {
                    com.recv_msg.appendText("(公共信息)玩家" + msg.getFromPlayer().getName() + "退出聊天室\r\n");

                } else if (msg.getMsgType() == MessageType.SENDFILES) {
                    Chess5FileRec chess5FileRec = new Chess5FileRec();
                    chess5FileRec.start();
                    String str = chess5FileRec.getServerSocket();
                    System.out.println(str);
                    Message recMsg = new Message();
                    recMsg.setMsgType(25);
                    recMsg.setFromPlayer(localPlayer);
                    recMsg.setToPlayer(msg.getFromPlayer());
                    recMsg.setContent(str);
                    byte[] data = Message.convertToBytes(recMsg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);
                    com.recv_msg.appendText("(私聊信息)玩家" + msg.getFromPlayer().getName() + "向您发送了文件\r\n");
                } else if (msg.getMsgType() == MessageType.SENDFILES_RSP) {

                    file = 1;
                    addr = msg.getContent().toString();
                }else if(msg.getMsgType()==MessageType.GUIZHAN){
                    System.out.println("观战收到的信息 "+msg.getContent());

                    String content = msg.getContent().toString().replaceAll("\\[", "").replaceAll("\\]", "");

                    String[] dataT = content.split(",");
                    int column = Integer.parseInt(dataT[0]);
                    int row = Integer.parseInt(dataT[1]);
                    int color = Integer.parseInt(dataT[2]);
                    int action = Integer.parseInt(dataT[3]);
                    int[][] data=paneThread.pane.getData();
                    if (data[row][column]==0){
                        paneThread.drawGZ(row, column, color);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
