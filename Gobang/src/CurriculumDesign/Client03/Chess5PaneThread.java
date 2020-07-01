package CurriculumDesign.Client03;

import CurriculumDesign.Server.Message;
import CurriculumDesign.Server.MessageType;
import CurriculumDesign.Server.Player;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Chess5PaneThread extends Thread {

    DatagramSocket client;

    Chess5Pane pane;
    Label lbl_name;
    Button btn_join_game, btn_join_host, btn_update_host, btn_challenge, btn_clear, btn_huiqi, btn_dofall, btn_surrend, btn_send_msg, btn_url, btn_save_pane, btn_information, btn_guanzhan, btn_quit_host;
    ChoiceBox<String> cBox;//用于擂主列表
    RadioButton rbtn_white, rbtn_black;
    ObservableList options = null;
    DatagramPacket pout;

    public Chess5PaneThread(DatagramSocket client, Label lbl_name, Button btn_join_game, Button btn_join_host, Button btn_update_host, Button btn_challenge, ChoiceBox<String> cBox, Chess5Pane pane, RadioButton rbtn_white, RadioButton rbtn_black, Button btn_clear, Button btn_huiqi, Button btn_dofall, Button btn_surrend, Button btn_send_msg, Button btn_url, Button btn_save_pane, Button btn_information, Button btn_guanzhan, Button btn_quit_host) {
        this.client = client;
        this.lbl_name = lbl_name;
        this.btn_join_game = btn_join_game;
        this.btn_join_host = btn_join_host;
        this.btn_update_host = btn_update_host;
        this.btn_challenge = btn_challenge;
        this.cBox = cBox;
        this.pane = pane;
        this.rbtn_black = rbtn_black;
        this.rbtn_white = rbtn_white;
        this.btn_clear = btn_clear;
        this.btn_huiqi = btn_huiqi;
        this.btn_dofall = btn_dofall;
        this.btn_surrend = btn_surrend;
        this.btn_send_msg = btn_send_msg;
        this.btn_url = btn_url;
        this.btn_save_pane = btn_save_pane;
        this.btn_information = btn_information;
        this.btn_guanzhan = btn_guanzhan;
        this.btn_quit_host = btn_quit_host;
    }

    public void gameOver() {
        btn_join_host.setDisable(true);
        btn_quit_host.setDisable(false);
        btn_update_host.setDisable(false);
        cBox.setDisable(false);
        btn_challenge.setDisable(false);
        btn_guanzhan.setDisable(false);
        rbtn_white.setDisable(false);
        rbtn_black.setDisable(false);
        btn_information.setDisable(false);
        btn_send_msg.setDisable(false);
        btn_clear.setDisable(false);
        btn_huiqi.setDisable(true);
        btn_dofall.setDisable(true);
        btn_surrend.setDisable(true);
        btn_save_pane.setDisable(false);
        btn_url.setDisable(false);

    }

    public void drawGZ(int row, int column, int color) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.drawChess(row, column, color);
                pane.setData(pane.getData());
                if (Judgement.judge(pane.getData()) == color) {
                    if (color == 1) {
                        JOptionPane.showMessageDialog(null, "黑棋获胜", "提示", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "白棋获胜", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                    gameOver();
//                    pane.clear();
                    pane.setMouseTransparent(true);
                    Message msg = new Message();
                    Player me =new Player(lbl_name.getText(),null);
                    msg.setFromPlayer(me);
                    msg.setMsgType(27);
                    byte[] data = Message.convertToBytes(msg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    try {
                        client.send(pout);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void draw(int row, int column, int color) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.drawChess(row, column, color);
                pane.setData(pane.getData());

                if (Judgement.judge(pane.getData()) == pane.getCompetition().getLocalPlayer().getColor()) {
                    String str = "游戏结束！我赢了。";
                    JOptionPane.showMessageDialog(null, str, "提示", JOptionPane.PLAIN_MESSAGE);
                    try {
                        Message msg = new Message();
                        msg.setFromPlayer(pane.getCompetition().getLocalPlayer());
                        msg.setToPlayer(pane.getCompetition().getRemotePlayer());
                        msg.setMsgType(0);

                        byte[] data = Message.convertToBytes(msg);
                        pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                        client.send(pout);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    pane.competition.setResult(0);

                    gameOver();
                    pane.setMouseTransparent(true);

                } else if (Judgement.judge(pane.getData()) == pane.getCompetition().getRemotePlayer().getColor()) {
                    String str = "游戏结束！我输了。";
                    JOptionPane.showMessageDialog(null, str, "提示", JOptionPane.PLAIN_MESSAGE);
//                    pane.competition.setResult(1);
                    try {
                        Message msg = new Message();
                        msg.setFromPlayer(pane.getCompetition().getLocalPlayer());
                        msg.setToPlayer(pane.getCompetition().getRemotePlayer());
                        msg.setMsgType(MessageType.FAIL);
                        byte[] data = Message.convertToBytes(msg);
                        pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                        client.send(pout);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pane.competition.setResult(0);
                    gameOver();
                    pane.setMouseTransparent(true);

                } else {
                    pane.setMouseTransparent(false);
//                    continue;
                }

            }
        });

    }

    public void setcBox(ObservableList options) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cBox.setItems(options);
            }
        });
    }
    public void reClear() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.clear();
            }
        });
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

    public int[][] getData() {
        return pane.getData();
    }

    public void run() {
        while (true) {
            if (pane.getCompetition() != null) {

                int result=pane.getCompetition().getResult();

                System.out.println(result);
                if (result != 0) {
                    gameOver();
                    if(result==pane.getCompetition().getLocalPlayer().getColor()){
                        JOptionPane.showMessageDialog(null, "游戏结束！我赢了。", "提示", JOptionPane.PLAIN_MESSAGE);
                        pane.getCompetition().setResult(0);
                        gameOver();
                    }else{
                        JOptionPane.showMessageDialog(null, "游戏结束！我输了。", "提示", JOptionPane.PLAIN_MESSAGE);
                        pane.getCompetition().setResult(0);

                        gameOver();

                    }


                }
            }

        }
    }
}
