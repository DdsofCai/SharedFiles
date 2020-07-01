package CurriculumDesign.Client;

import CurriculumDesign.Server.DataBase;
import CurriculumDesign.Server.Message;
import CurriculumDesign.Server.Player;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5GUI extends Application {

    //	new
    DatagramSocket client = null;
    DataBase db;
    DatagramPacket pout = null;
    Chess5ListenThread chess5ListenThread;
    Chess5PaneThread paneThread;
    Chess5Pane pane;//五子棋盘
    Label lbl_name;//用于显示玩家信息
    ChoiceBox<String> cBox;//用于擂主列表
    Button btn_guanzhan, btn_quit_host, btn_join_game, btn_url, btn_clear, btn_huiqi, btn_join_host, btn_update_host, btn_challenge, btn_send_msg, btn_save_pane, btn_public_com, btn_surrend, btn_dofall, btn_information;
    RadioButton rbtn_white, rbtn_black;
    Player localPlayer;
    Chess5Com com;

    public Chess5GUI(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // TODO Auto-generated method stub

        pane = new Chess5Pane();
        pane.setMouseTransparent(true);//屏蔽棋盘上鼠标事件
        db.connectDB();
// 组件
        btn_join_game = new Button("加入游戏");
        btn_quit_host = new Button("退出擂台");
        btn_join_host = new Button("加入擂台");
        btn_update_host = new Button("更新擂台");
        btn_send_msg = new Button("聊天室");
//        btn_public_com = new Button("群聊");
        btn_save_pane = new Button("保存棋盘");
        cBox = new ChoiceBox<String>();
        btn_challenge = new Button("发起挑战");
        rbtn_white = new RadioButton("白子先手");
        rbtn_black = new RadioButton("黑子后手");
        btn_clear = new Button("清理棋盘");
        btn_surrend = new Button("投降");
        btn_dofall = new Button("求和");
        btn_information = new Button("查看信息");
        ToggleGroup tg = new ToggleGroup();
        lbl_name = new Label("我是一个匆匆过客......");
        btn_huiqi = new Button("悔棋");
        btn_url = new Button("链接");
        btn_guanzhan = new Button("观战");
//        面板一
        FlowPane fPane_01 = new FlowPane();
        fPane_01.setAlignment(Pos.CENTER);
        fPane_01.setMinHeight(70);
        fPane_01.setHgap(10);

        for (int n = 0; n < 5; n++) {
            Circle c = new Circle(20 - n * 2, Color.BLACK);
            fPane_01.getChildren().add(c);
        }


        fPane_01.getChildren().add(btn_join_host);
        fPane_01.getChildren().add(btn_quit_host);
        for (int n = 0; n < 5; n++) {
            Circle c = new Circle(12 + n * 2, Color.WHITE);
            fPane_01.getChildren().add(c);
        }


        // 组件属性设置
        rbtn_white.setToggleGroup(tg);
        rbtn_white.setSelected(true);
        rbtn_black.setToggleGroup(tg);

        btn_join_host.setDisable(false);
        btn_quit_host.setDisable(true);
        btn_update_host.setDisable(true);
        cBox.setDisable(true);
        btn_challenge.setDisable(true);
        btn_guanzhan.setDisable(true);
        rbtn_white.setDisable(true);
        rbtn_black.setDisable(true);
        btn_information.setDisable(false);
        btn_send_msg.setDisable(false);
        btn_clear.setDisable(true);
        btn_huiqi.setDisable(true);
        btn_dofall.setDisable(true);
        btn_surrend.setDisable(true);
        btn_save_pane.setDisable(true);
        btn_url.setDisable(false);

        // 事件
        btn_join_host.setOnAction(this::btnJoinHostHandler);
        btn_quit_host.setOnAction(this::btnQuitHostHandler);
        btn_challenge.setOnAction(this::btnChallengeHandler);
        btn_update_host.setOnAction(this::btnUpdateHostHandler);
        btn_save_pane.setOnAction(this::btnSavePaneHandler);
        btn_huiqi.setOnAction(this::btnHQHandler);
        btn_dofall.setOnAction(this::btnDoFallHandler);
        btn_surrend.setOnAction(this::btnSurrenderHandler);
        btn_send_msg.setOnAction(this::btnSendMsgHandler);
        btn_url.setOnAction(this::btnOpenUrl);
        btn_clear.setOnAction(this::btnClearPane);
        btn_information.setOnAction(this::btnInformation);
        btn_guanzhan.setOnAction(this::btnGuanZhan);
        // 面板二
        FlowPane fPane_02 = new FlowPane(btn_update_host, cBox, btn_challenge, btn_guanzhan, rbtn_white, rbtn_black);
        fPane_02.setAlignment(Pos.CENTER);
        fPane_02.setHgap(50);

        client = new DatagramSocket(ClientConfig.LOCAL_ADDR);
        client.connect(ClientConfig.SERVER_ADDR);


        //启动控制监听线程
        //主面板线程
        paneThread = new Chess5PaneThread(client, lbl_name, btn_join_game, btn_join_host, btn_update_host, btn_challenge, cBox, pane, rbtn_white, rbtn_black, btn_clear, btn_huiqi, btn_dofall, btn_surrend, btn_send_msg,btn_url,btn_save_pane,btn_information,btn_guanzhan,btn_quit_host);
        paneThread.start();

        //启动面板
        com = new Chess5Com(client, localPlayer, paneThread);
        btnJoinGameHandler();

        chess5ListenThread = new Chess5ListenThread(client, paneThread, com);
        chess5ListenThread.start();
        //  面板三
        FlowPane fPane_03 = new FlowPane(lbl_name, btn_information, btn_send_msg);
        fPane_03.setAlignment(Pos.CENTER);
        fPane_03.setHgap(30);
        //  面板四
//        FlowPane fPane_04 = new FlowPane();
//        fPane_04.setAlignment(Pos.CENTER);
//        fPane_04.setHgap(40);
        //  面板五
        FlowPane fPane_05 = new FlowPane(btn_clear, btn_huiqi, btn_dofall, btn_surrend, btn_save_pane, btn_url);
        fPane_05.setAlignment(Pos.CENTER);
        fPane_05.setHgap(40);


        //  主面板
        VBox box = new VBox(fPane_01, fPane_02, fPane_03, pane, fPane_05);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        box.setStyle("-fx-border-color:#CDAA7D;-fx-background-color:#CDAA7D;-fx-border-width:5");
//        box.setStyle("-fx-border-color:grey;-fx-background-color:#cb99c5;-fx-border-width:5");
        Scene scene = new Scene(box, 650, 820);
        stage.setScene(scene);
        stage.setTitle("Chess5 GUI");
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                try {
                    Message msg = new Message();
                    msg.setMsgType(23);
                    msg.setFromPlayer(localPlayer);
                    byte[] data = Message.convertToBytes(msg);
                    pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                    client.send(pout);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.print("监听到窗口关闭");
                System.exit(0);
            }
        });

    }


    //截图 并保存
    public static void captureScreen(String folder,String fileName)throws Exception{
        //获得屏幕大小并创建一个Rectangle(区域)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        //创建包含从屏幕中读取的像素的图像
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRectangle);
        //保存路径
        File screenFile = new File(folder);
        if(!screenFile.exists()) {
            screenFile.mkdir();
        }
        File f = new File(screenFile,fileName);
        ImageIO.write(image, "png", f);
    }

    //观战
    private void btnGuanZhan(ActionEvent actionEvent) {
        try {
            pane.clear();
            Message msg = new Message();
            msg.setMsgType(26);
            msg.setFromPlayer(localPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
            btn_guanzhan.setDisable(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //退出擂台
    private void btnQuitHostHandler(ActionEvent actionEvent) {
        try {
            Message msg = new Message();
            msg.setMsgType(22);
            msg.setFromPlayer(localPlayer);

            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
            lbl_name.setText(localPlayer.getName());
            btn_join_host.setDisable(false);
            btn_quit_host.setDisable(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        btn_join_host.setDisable(false);
        btn_quit_host.setDisable(true);
        btn_update_host.setDisable(true);
        cBox.setDisable(true);
        btn_challenge.setDisable(true);
        btn_guanzhan.setDisable(true);
        rbtn_white.setDisable(true);
        rbtn_black.setDisable(true);
        btn_information.setDisable(false);
        btn_send_msg.setDisable(false);
        btn_clear.setDisable(true);
        btn_huiqi.setDisable(true);
        btn_dofall.setDisable(true);
        btn_surrend.setDisable(true);
        btn_save_pane.setDisable(true);
        btn_url.setDisable(false);
    }

    //进入聊天室
    public void btnSendMsgHandler(ActionEvent event) {

        try {
            btn_send_msg.setDisable(true);
            com.start(new Stage());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    //加入游戏
    public void btnJoinGameHandler() {
        try {
            Message msg = new Message();
            msg.setMsgType(1);
            msg.setFromPlayer(localPlayer);

            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
            lbl_name.setText(localPlayer.getName());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //加入擂台
    public void btnJoinHostHandler(ActionEvent e) {
        try {
            Message msg = new Message();
            msg.setMsgType(2);
            msg.setFromPlayer(localPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        btn_quit_host.setDisable(false);
        System.out.println("加入擂台");

    }

    //更新擂主
    public void btnUpdateHostHandler(ActionEvent e) {
        try {
            Message msg = new Message();
            msg.setMsgType(5);
            msg.setFromPlayer(localPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //发起挑战
    public void btnChallengeHandler(ActionEvent e) {
        String target = cBox.getValue();

        if (target.equals(localPlayer.getName())) {
            JOptionPane.showMessageDialog(null, "不能向自己发起挑战", "警告", JOptionPane.PLAIN_MESSAGE);
        } else if (target.equals("擂主列表")) {
            JOptionPane.showMessageDialog(null, "请选择擂主", "警告", JOptionPane.PLAIN_MESSAGE);
        } else {
            try {
                pane.clear();
                //被挑战玩家信息
                Player toPlayer = new Player(target, null);
                if (rbtn_white.isSelected()) { //1黑2白
                    localPlayer.setColor(2);
                } else {
                    localPlayer.setColor(1);
                }
                Message msg = new Message();
                msg.setMsgType(3);
                msg.setFromPlayer(localPlayer);
                msg.setToPlayer(toPlayer);
                byte[] data = Message.convertToBytes(msg);
                pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                client.send(pout);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    //求和
    public void btnDoFallHandler(ActionEvent actionEvent) {

        try {
            Competition competition = pane.getCompetition();
            Player toPlayer = competition.getRemotePlayer();
            Message msg = new Message();
            msg.setMsgType(11);
            msg.setFromPlayer(localPlayer);
            msg.setToPlayer(toPlayer);
            byte[] data = Message.convertToBytes(msg);
            pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
            client.send(pout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //投降
    public void btnSurrenderHandler(ActionEvent actionEvent) {
        int n = JOptionPane.showConfirmDialog(null, "确定投降吗", "警告", JOptionPane.YES_NO_OPTION);//1拒绝-0接受
        if (n == 0) {
            try {
                Message msg = new Message();
                msg.setMsgType(10);
                msg.setFromPlayer(localPlayer);
                msg.setToPlayer(pane.getCompetition().getRemotePlayer());
                byte[] data = Message.convertToBytes(msg);
                pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                client.send(pout);
                pane.setMouseTransparent(true);
                paneThread.gameOver();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //悔棋
    public void btnHQHandler(ActionEvent actionEvent) {
        int n = JOptionPane.showConfirmDialog(null, "确定悔棋吗", "警告", JOptionPane.YES_NO_OPTION);//1拒绝-0接受
        if (n == 0) {
            try {
                Message msg = new Message();
                msg.setMsgType(8);
                msg.setFromPlayer(localPlayer);
                msg.setToPlayer(pane.getCompetition().getRemotePlayer());
                byte[] data = Message.convertToBytes(msg);
                pout = new DatagramPacket(data, data.length, ClientConfig.SERVER_ADDR);
                client.send(pout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //查看我的信息
    public void btnInformation(ActionEvent actionEvent) {
        String information = db.selectUser(localPlayer.getName());
        JOptionPane.showMessageDialog(null, information, "我的信息", JOptionPane.PLAIN_MESSAGE);
    }

    //清理棋盘
    public void btnClearPane(ActionEvent actionEvent) {
        pane.clear();
    }

    //保存棋盘
    public void btnSavePaneHandler(ActionEvent e) {

        int[][] data = pane.getData();
        File f = new File("d://A.txt");
        FileOutputStream fos = null;
        try {
            String str = "";
            fos = new FileOutputStream(f);
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    str = str + data[i][j] + ",";
                    System.out.print(data[i][j] + ",");
                }
            }
            fos.write(str.getBytes());
            fos.close();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String files=localPlayer.getName()+"_Data";
        try {
            captureScreen(files,"data.png");
        }catch(Exception ex){
            ex.printStackTrace();
        }


    }

    //打开链接
    public void btnOpenUrl(ActionEvent actionEvent) {
        if (java.awt.Desktop.isDesktopSupported()) {
            try {
                // 创建一个URI实例
                java.net.URI uri = java.net.URI.create("http://www.wuzi8.com/");
                // 获取当前系统桌面扩展
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                // 判断系统桌面是否支持要执行的功能
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    // 获取系统默认浏览器打开链接
                    dp.browse(uri);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}