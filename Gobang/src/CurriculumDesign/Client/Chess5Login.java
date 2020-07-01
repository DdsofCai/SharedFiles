package CurriculumDesign.Client;

import CurriculumDesign.Server.DataBase;
import CurriculumDesign.Server.Player;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.*;
import java.net.SocketException;
import java.security.MessageDigest;

/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5Login extends Application {
    TextField textUsername;
    PasswordField textPassword;

    @Override
    public void start(Stage primaryStage) throws SocketException {

        //面板
        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();

        //组件
        Button btn_login = new Button("登录");
        Button btn_register = new Button("注册");
        Button btn_close = new Button("关闭");
        textUsername = new TextField();
        textUsername.setPromptText("");
        textPassword = new PasswordField();
        textPassword.setPromptText("");

        //布局
        gridPane.add(new Label("账号:"), 0, 0, 1, 1);
        gridPane.add(textUsername, 1, 0);
        gridPane.add(new Label("密码:"), 0, 1, 1, 1);
        gridPane.add(textPassword, 1, 1);
        gridPane.add(btn_register, 2, 1);
        gridPane.add(btn_login, 2, 0);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
//        client = new DatagramSocket(ClientConfig.LOCAL_ADDR);
//        client.connect(ClientConfig.SERVER_ADDR);
        stackPane.getChildren().add(gridPane);
        btn_login.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                String name=textUsername.getText();
                String password=textPassword.getText();

                DataBase db=new DataBase();
                db.connectDB();
//                System.out.println(db.checkUser(name,password));
                if(db.checkUser(name,convertMD5(password))==1){
                    primaryStage.close();

                    JOptionPane.showMessageDialog(null, "欢迎"+textUsername.getText()+"玩家进入游戏", "进入游戏成功", JOptionPane.PLAIN_MESSAGE);
                    Player player=new Player(textUsername.getText(), ClientConfig.LOCAL_ADDR);
                    Chess5GUI chess5GUI=new Chess5GUI(player);
                    try {
                        chess5GUI.start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    JOptionPane.showMessageDialog(null, "账号或密码错误", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        btn_register.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                try {
                    Chess5Register register=new Chess5Register();
                    register.start(new Stage());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        });
        stackPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(stackPane, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess5 login screen");
        primaryStage.show();
    }
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }


    public static void main(String[] args) {
        launch(args);
    }


}
