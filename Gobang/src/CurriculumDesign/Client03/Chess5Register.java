package CurriculumDesign.Client03;


import CurriculumDesign.Server.DataBase;
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
import java.net.DatagramSocket;
import java.net.SocketException;

public class Chess5Register extends Application {
    DatagramSocket client;
    Button btn_login, btn_register, btn_close;
    TextField textUsername,textEmail;
    PasswordField textPassword,textPassword2;

    @Override
    public void start(Stage primaryStage) throws SocketException {
        //Panes
        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();

        //Nodes
        Button btn_login = new Button("关闭");
        Button btn_register = new Button("注册");
//        Button btn_close = new Button("关闭");

        textUsername = new TextField();
        textUsername.setPromptText("");
        textEmail = new TextField();
        textEmail.setPromptText("");
        textPassword = new PasswordField();
        textPassword.setPromptText("");
        textPassword2 = new PasswordField();
        textPassword2.setPromptText("");
        //Panes
        gridPane.add(new Label("用户名:"), 0, 0, 1, 1);
        gridPane.add(textUsername, 1, 0);
        gridPane.add(new Label("密码:"), 0, 1, 1, 1);
        gridPane.add(textPassword, 1, 1);
        gridPane.add(new Label("确认密码"),0,2,1,1);
        gridPane.add(textPassword2, 1, 2);
        gridPane.add(btn_login,2,2);
        gridPane.add(new Label("电子邮箱"),0, 3,1,1);
        gridPane.add(textEmail,1,3,1,1);
        gridPane.add(btn_register, 2, 3,1,1);

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        stackPane.getChildren().add(gridPane);
        btn_register.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                String name=textUsername.getText();
                String password=textPassword.getText();
                String password2=textPassword2.getText();
                String email=textEmail.getText();

                if(name.isEmpty()||password.isEmpty()||password2.isEmpty()||email.isEmpty()){
                    JOptionPane.showMessageDialog(null, "账号密码邮箱均不能为空", "提示", JOptionPane.PLAIN_MESSAGE);
                }
                else if(password.equals(password2)==false){
                    JOptionPane.showMessageDialog(null, "两次密码不一致", "提示", JOptionPane.PLAIN_MESSAGE);

                }else{

                    DataBase db=new DataBase();
                    db.connectDB();
                    int checkNameResult=db.checkName(name);
                    if(checkNameResult==0){
                        db.addUser(name,password2,email);
                        JOptionPane.showMessageDialog(null, "注册成功", "提示", JOptionPane.PLAIN_MESSAGE);
                        primaryStage.close();

                    }else{
                        JOptionPane.showMessageDialog(null, "该用户名已经存在，请重新注册。", "提示", JOptionPane.PLAIN_MESSAGE);

                    }
                }

            }
        });
        btn_login.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
//                try {
                    primaryStage.close();
//                    Chess5Login login=new Chess5Login();
//                    login.start(new Stage());
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
            }});

        stackPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(stackPane, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess5 registration screen ");
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }


}