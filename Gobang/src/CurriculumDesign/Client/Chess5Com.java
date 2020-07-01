package CurriculumDesign.Client;

import CurriculumDesign.Server.Message;
import CurriculumDesign.Server.Player;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5Com extends Application {
    DatagramSocket client;
    DatagramPacket pout;
    Button btn_send, btn_look, btn_share, btn_fresh, btn_quit;
    TextField tf_msg;
    TextArea recv_msg;
    Player localPlayer;
    ComboBox<String> cBox;
    ListView<String> list;
    Chess5PaneThread paneThread;

    public Chess5Com(DatagramSocket client, Player localPlayer, Chess5PaneThread paneThread) {
        this.client = client;
        this.localPlayer = localPlayer;
        this.paneThread = paneThread;
    }

    @Override
    public void start(Stage primaryStage) throws SocketException {

        //面板
        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();

        //组件
        list = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(
                "Single", "Double", "Suite", "Family App");
        list.setItems(items);
        list.setPrefWidth(100);
        list.setPrefHeight(70);
        cBox = new ComboBox<String>();
        tf_msg = new TextField();
        tf_msg.setPromptText("");
        recv_msg = new TextArea();
        recv_msg.setPromptText("");
        btn_share = new Button("分享");
        btn_send = new Button("发送");
        btn_look = new Button("查看");
        btn_fresh = new Button("刷新");
        btn_quit = new Button("退出");
        //事件监听
        btn_fresh.setOnAction(this::btnFreshCom);
        btn_send.setOnAction(this::btnSend);
        btn_quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                try {
                    Message msg = new Message();
                    msg.setFromPlayer(localPlayer);
                    msg.setMsgType(21);
                    byte[] data = Message.convertToBytes(msg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);
                    primaryStage.close();
                    paneThread.btn_send_msg.setDisable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        //布局
        gridPane.add(new Label("聊天记录"), 0, 0, 3, 1);
        gridPane.add(recv_msg, 0, 1, 4, 3);
        gridPane.add(new Label("在线玩家"), 4, 0, 1, 1);
        gridPane.add(list, 4, 1, 1, 3);
        gridPane.add(new Label("消息"), 0, 4, 1, 1);
        gridPane.add(tf_msg, 1, 4, 3, 1);
        gridPane.add(cBox, 0, 5, 1, 1);
        gridPane.add(btn_send, 1, 5, 1, 1);
        gridPane.add(btn_share, 2, 5, 1, 1);
        gridPane.add(btn_look, 3, 5, 1, 1);
        gridPane.add(btn_fresh, 4, 4, 1, 1);
        gridPane.add(btn_quit, 4, 5, 1, 1);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        btn_look.setOnAction(this::btnLook);
        stackPane.getChildren().add(gridPane);

        stackPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(stackPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess5 communicate screen");
        try {
            Message msg = new Message();
            msg.setMsgType(15);
            msg.setFromPlayer(localPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnFreshCom();
        primaryStage.show();
        btn_share.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

                try {
                    Message msg = new Message();
                    msg.setMsgType(24);
                    msg.setFromPlayer(localPlayer);
                    Player toPlayer = new Player(cBox.getValue(), null);
                    msg.setToPlayer(toPlayer);
                    byte[] data = Message.convertToBytes(msg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);

                    Chess5FileChoose chess5FileChoose = new Chess5FileChoose();
                    chess5FileChoose.start(new Stage());

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        //监听该界面是否关闭
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    Message msg = new Message();
                    msg.setFromPlayer(localPlayer);
                    msg.setMsgType(21);
                    byte[] data = Message.convertToBytes(msg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);
                    primaryStage.close();
                    paneThread.btn_send_msg.setDisable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    //查看
    private void btnLook(ActionEvent actionEvent) {
        try {
            Runtime.getRuntime().exec("explorer.exe D:\\Project\\Java\\IDEA");
        } catch (Exception e) {
        }
    }

    //发送
    public void btnSend(ActionEvent actionEvent) {
        try {
            String name = cBox.getValue();
            String content = tf_msg.getText();
            if (name.equals(localPlayer.getName()) || name.isEmpty() || name == null) {
                JOptionPane.showMessageDialog(null, "请选择私信对象，不能选择自己或者空对象", "提示", JOptionPane.PLAIN_MESSAGE);
            } else {
                Message msg = new Message();
                msg.setFromPlayer(localPlayer);
                if (name.equals("公共频道")) {
                    msg.setMsgType(16);
                } else {
                    recv_msg.appendText("(私聊信息)玩家" + localPlayer.getName() + "向" + name + "发送" + content + "\r\n");
                    msg.setMsgType(17);
                    Player toPlayer = new Player(name, null);
                    msg.setToPlayer(toPlayer);
                }
                msg.setContent(content);
                byte[] data = Message.convertToBytes(msg);
                pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                client.send(pout);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //刷新
    public void btnFreshCom(ActionEvent actionEvent) {
        try {
            Message msg = new Message();
            msg.setMsgType(20);
            msg.setFromPlayer(localPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //刷新
    public void btnFreshCom() {
        try {
            Message msg = new Message();
            msg.setMsgType(20);
            msg.setFromPlayer(localPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


}
