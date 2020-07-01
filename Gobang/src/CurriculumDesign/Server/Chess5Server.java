package CurriculumDesign.Server;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5Server {
    private static int[][] paneIntData = new int[10][10];
    ArrayList<String> paneData = new ArrayList<String>();
    ArrayList<String> pre = new ArrayList<String>();
    ArrayList<int[][]> currentData = new ArrayList<int[][]>();
    ArrayList<Player> guanZhanPlayer = new ArrayList<Player>();
    DatagramSocket socket = new DatagramSocket(ServerConfig.SERVER_ADDR);
    DatagramPacket pout = null;

    public static void main(String[] args) throws SocketException {
        // TODO Auto-generated method stub
        Chess5Server cs = new Chess5Server();
        cs.service();
    }

    public Chess5Server() throws SocketException {
        init();
    }

    public int[][] getCurrentPane() {
        int[][] c = currentData.get(currentData.size() - 1);
        return c;
    }

    public void init() {
        Chess5Update chess5Update = new Chess5Update(socket, pout);
        chess5Update.start();
    }

    public static String getPaneIntData() {
        return printArr1(paneIntData);
    }

    public void setInitPaneIntData() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                paneIntData[i][j] = 0;
            }
        }
    }

    public static String printArr1(int[][] arr) {
        String str = "";
        for (int x = 0; x < arr.length; x++) {
            for (int y = 0; y < arr[x].length; y++) {
                str = str + arr[x][y] + ",";
            }
            str += "-";
        }
        return str;
    }


    public void service() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                paneIntData[i][j] = 0;
            }
        }
//        (DatagramSocket socket = new DatagramSocket(ServerConfig.SERVER_ADDR);//172.16.134.2:8000
//        )
        try {
            while (true) {
                try {
                    DataBase dataBase = new DataBase();
                    dataBase.connectDB();

                    DatagramPacket pin = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(pin);
                    Message msg = (Message) Message.convertToObj(pin.getData(), 0, pin.getLength());
                    Message rspMsg = new Message();

                    /**
                     * 1.如果是“JOIN_GAME”，表示有用户要加入游戏，则检查昵称是否已经存在，如果存在，则拒绝；将当前的擂主名单返回给新加入玩家；
                     * 2.如果是“JOIN_HOST”，表示有玩家要加入擂台，则检查是否已经加入，如果是，则拒绝；将当前的擂主名单返回给新加入擂台的玩家；
                     * 3.如果是“CHALLENGE”，表示有玩家发出了一个挑战擂主的请求，将该请求转发给对应的擂主；
                     * 4.如果是“CHALLENGE_RSP”，表示有被挑战的擂主的回复了挑战请求，将该回复转发给发起挑战的玩家；
                     * 5.如果是“PLAY”，表示正在比赛中的一方下了棋子，发出一个请求告诉对方棋子的信息，服务器转发该信息给另一方。
                     */

                    if (msg.getMsgType() == MessageType.JOIN_GAME) {

                        //判断玩家姓名是否存在

                        if (ServerConfig.containPlayer(msg.getFromPlayer().getName())) {
                            //存在,则加入游戏失败
                            rspMsg.setStatus(0);
                            rspMsg.setMsgType(1);
                            byte[] data = Message.convertToBytes(rspMsg);
                            pout = new DatagramPacket(data, data.length, msg.getFromPlayer().getAddress());
                            socket.send(pout);
                        } else {
                            //不存在,则加入游戏成功
                            //服务器添加该玩家的信息
                            Player player = msg.getFromPlayer();
                            ServerConfig.addPlayer(player.getName(), player);
                            rspMsg.setStatus(1);
                            rspMsg.setMsgType(1);
                            rspMsg.setToPlayer(player);
                            byte[] data = Message.convertToBytes(rspMsg);
                            pout = new DatagramPacket(data, data.length, player.getAddress());
                            socket.send(pout);
                        }

                    } else if (msg.getMsgType() == MessageType.JOIN_HOST) {
                        Player player = msg.getFromPlayer();
                        rspMsg.setMsgType(2);
                        rspMsg.setToPlayer(player);
                        if (ServerConfig.containHost(player.getName())) {
                            rspMsg.setStatus(0);
                        } else {
                            rspMsg.setStatus(1);
                            ServerConfig.addHost(player.getName(), player);
                        }
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, player.getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.UPDATE_HOST) {
                        Player player = msg.getFromPlayer();
                        rspMsg.setMsgType(5);
                        rspMsg.setContent("擂主列表:" + ServerConfig.getHostNamesList());
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, player.getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.CHALLENGE) {

                        //发起挑战的玩家 challenge 被挑战的玩家信息  challenged
                        Player challenge = msg.getFromPlayer();
                        Player challenged = ServerConfig.getPlayer(msg.getToPlayer().getName());//在配置文件中获取信息
                        if (ServerConfig.containPlaying(challenged.getName())) {
                            System.out.println("该玩家正在游戏中");
                            Message msgC = new Message();
                            msgC.setFromPlayer(challenge);
                            msgC.setToPlayer(challenged);
                            msgC.setStatus(-1);
                            msgC.setMsgType(4);
                            byte[] data = Message.convertToBytes(msgC);
                            pout = new DatagramPacket(data, data.length, challenge.getAddress());
                            socket.send(pout);
                        } else {
                            System.out.println("该玩家不在游戏中");
                            System.out.println(challenge.getName() + "--挑战->" + challenged.getName());
                            Message msgC = new Message();
                            msgC.setFromPlayer(challenge);
                            msgC.setToPlayer(challenged);
                            msgC.setMsgType(3);
                            byte[] data = Message.convertToBytes(msgC);
                            pout = new DatagramPacket(data, data.length, challenged.getAddress());
                            socket.send(pout);
                        }
                    } else if (msg.getMsgType() == MessageType.CHALLENGE_RSP) {
                        int result = msg.getStatus();
                        Player challenged = msg.getFromPlayer();
                        Player challenge = msg.getToPlayer();
                        rspMsg.setFromPlayer(challenged);
                        rspMsg.setToPlayer(challenge);
                        rspMsg.setMsgType(4);
                        //接受挑战
                        if (result == 1) {
                            System.out.println(challenged.getName() + "--接受挑战->" + challenge.getName());
                            rspMsg.setStatus(1);
                            ServerConfig.addPlaying(challenge.getName(), challenge);
                            ServerConfig.addPlaying(challenged.getName(), challenged);
                        } else {
                            System.out.println(challenged.getName() + "--不接受挑战->" + challenge.getName());

                            rspMsg.setStatus(0);
                        }

                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, challenge.getAddress());
                        socket.send(pout);

                    } else if (msg.getMsgType() == MessageType.PLAY) {

                        String panData = msg.getContent().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                        String[] content = panData.split(",");
                        paneIntData[Integer.parseInt(content[1])][Integer.parseInt(content[0])] = Integer.parseInt(content[2]);
                        currentData.add(paneIntData);
                        System.out.println(printArr1(paneIntData));
                        rspMsg.setMsgType(6);
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setContent(msg.getContent());
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);
                        pre.add(printArr1(getCurrentPane()));//pre.get(pre.size()-2)上一步

                    } else if (msg.getMsgType() == MessageType.SURRENDER) {
                        rspMsg.setMsgType(10);
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setFromPlayer(msg.getFromPlayer());

                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);
                        dataBase.delExc(msg.getFromPlayer().getName());
                        dataBase.addExc(msg.getToPlayer().getName());
                        ServerConfig.delPlaying(msg.getFromPlayer().getName());
                        ServerConfig.delPlaying(msg.getToPlayer().getName());
                    } else if (msg.getMsgType() == MessageType.HUIQI) {

                        rspMsg.setMsgType(8);
                        rspMsg.setContent(pre.get(pre.size() - 2));
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.HUIQI_RSP) {
                        int result = msg.getStatus();
                        rspMsg.setMsgType(9);
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        if (result == 1) {
                            rspMsg.setStatus(1);
                            rspMsg.setContent(paneData.get(paneData.size() - 1));
                            dataBase.addExc(msg.getFromPlayer().getName());


                        } else {
                            rspMsg.setStatus(0);
                        }
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);

                    } else if (msg.getMsgType() == MessageType.DOGFALL) {
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setMsgType(11);
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.DOGFALL_RSP) {
                        int result = msg.getStatus();
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        if (result == 1) {
                            rspMsg.setStatus(1);
                            ServerConfig.delPlaying(msg.getFromPlayer().getName());
                            ServerConfig.delPlaying(msg.getToPlayer().getName());
                        } else {
                            rspMsg.setStatus(0);
                        }
                        rspMsg.setMsgType(12);
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.JOIN_COM) {
                        String name = msg.getFromPlayer().getName();
                        ServerConfig.addComPlayer(msg.getFromPlayer().getName(), msg.getFromPlayer());
//                        广播
                        HashMap<String, Player> map = ServerConfig.getPlayerComMap();
                        Iterator iter = map.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            Object key = entry.getKey();
                            rspMsg.setMsgType(15);
                            rspMsg.setContent(name);
                            byte[] data = Message.convertToBytes(rspMsg);
                            pout = new DatagramPacket(data, data.length, ServerConfig.getPlayerAddr(key.toString()));
                            socket.send(pout);
                        }
                    } else if (msg.getMsgType() == MessageType.UPDATE_COM) {
                        Player player = msg.getFromPlayer();
                        rspMsg.setMsgType(20);
                        rspMsg.setContent("公共频道:" + ServerConfig.getComNamesList());
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, player.getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.PUBLIC_CHAT) {
                        String content = msg.getContent().toString();
                        //                        广播
                        HashMap<String, Player> map = ServerConfig.getPlayerComMap();//h获取聊天室的人员
                        Iterator iter = map.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            Object key = entry.getKey();
                            rspMsg.setMsgType(16);
                            rspMsg.setFromPlayer(msg.getFromPlayer());
                            rspMsg.setContent(content);
                            byte[] data = Message.convertToBytes(rspMsg);
                            pout = new DatagramPacket(data, data.length, ServerConfig.getPlayerAddr(key.toString()));
                            socket.send(pout);
                        }
                        System.out.println("收到公共信息" + msg.getContent().toString());
                    } else if (msg.getMsgType() == MessageType.PRIVATE_CHAT) {
                        Player toPlayer = ServerConfig.getComPlayer(msg.getToPlayer().getName());
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        rspMsg.setMsgType(17);
                        rspMsg.setContent(msg.getContent());
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, toPlayer.getAddress());
                        socket.send(pout);
                        System.out.println("收到私聊信息" + msg.getContent().toString() + "对象是" + msg.getToPlayer().getName());
                    } else if (msg.getMsgType() == MessageType.QUIT_COM) {
                        String name = msg.getFromPlayer().getName();
                        ServerConfig.delComPlayer(msg.getFromPlayer().getName());
                        HashMap<String, Player> map = ServerConfig.getPlayerComMap();
                        Iterator iter = map.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            Object key = entry.getKey();
                            rspMsg.setMsgType(21);
                            rspMsg.setFromPlayer(msg.getFromPlayer());
                            byte[] data = Message.convertToBytes(rspMsg);
                            pout = new DatagramPacket(data, data.length, ServerConfig.getPlayerAddr(key.toString()));
                            socket.send(pout);
                        }
                    } else if (msg.getMsgType() == MessageType.SUCCESS) {
                        System.out.println("胜利者:" + msg.getFromPlayer());
                        System.out.println("失败者:" + msg.getToPlayer());
                        dataBase.addExc(msg.getFromPlayer().getName());
                        dataBase.delExc(msg.getToPlayer().getName());
                        ServerConfig.delPlaying(msg.getFromPlayer().getName());
                        ServerConfig.delPlaying(msg.getToPlayer().getName());
                        setInitPaneIntData();
                        System.out.println("重置棋盘 : " + printArr1(paneIntData));
                    } else if (msg.getMsgType() == MessageType.FAIL) {
                        System.out.println("失败:" + msg.getFromPlayer());
                        System.out.println("胜利者:" + msg.getToPlayer());
                        dataBase.delExc(msg.getFromPlayer().getName());
                        dataBase.addExc(msg.getToPlayer().getName());
                        ServerConfig.delPlaying(msg.getFromPlayer().getName());
                        ServerConfig.delPlaying(msg.getToPlayer().getName());
                        setInitPaneIntData();
                        System.out.println("重置棋盘 : " + printArr1(paneIntData));

                    } else if (msg.getMsgType() == MessageType.QUIT_HOST) {
                        ServerConfig.delHostPlayer(msg.getFromPlayer().getName());
                    } else if (msg.getMsgType() == MessageType.QUIT_GAME) {
                        ServerConfig.delAllPlayer(msg.getFromPlayer().getName());
                        System.out.println("删除该玩家所有信息");
                    } else if (msg.getMsgType() == MessageType.SENDFILES) {
                        System.out.println(msg.getFromPlayer().getName() + "发送文件请求" + msg.getToPlayer().getName());
                        Player toPlayer = ServerConfig.getPlayer(msg.getToPlayer().getName());
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        rspMsg.setToPlayer(toPlayer);
                        rspMsg.setMsgType(24);
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, toPlayer.getAddress());
                        socket.send(pout);
                        System.out.println("发送成功");
                    } else if (msg.getMsgType() == MessageType.SENDFILES_RSP) {
                        System.out.println(msg.getFromPlayer().getName() + "回应文件请求成功" + msg.getToPlayer().getName());
                        rspMsg.setMsgType(25);
                        rspMsg.setToPlayer(msg.getToPlayer());
                        rspMsg.setFromPlayer(msg.getFromPlayer());
                        rspMsg.setContent(msg.getContent());
                        byte[] data = Message.convertToBytes(rspMsg);
                        pout = new DatagramPacket(data, data.length, msg.getToPlayer().getAddress());
                        socket.send(pout);
                    } else if (msg.getMsgType() == MessageType.GUIZHAN) {
                        ServerConfig.addGuanZhanPlayer(msg.getFromPlayer().getName(), msg.getFromPlayer());//添加观战玩家
                        Chess5GuanZhan chess5GuanZhan = new Chess5GuanZhan(socket, printArr1(paneIntData));//启动观战线程
                        chess5GuanZhan.start();
//
                    } else if (msg.getMsgType() == MessageType.QUI_GUANZHAN) {
                        guanZhanPlayer.remove(ServerConfig.getPlayer(msg.getFromPlayer().getName()));
                    }

                } catch (Exception e) {
                    System.out.println("建立连接失败");
                }
            }

        } catch (Exception e) {
            System.out.println("开启服务失败");
        }

    }


}
