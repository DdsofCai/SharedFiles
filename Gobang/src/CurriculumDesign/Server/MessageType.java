package CurriculumDesign.Server;

/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class MessageType {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int JOIN_GAME = 1;//申请加入游戏的请求信息
    public static final int JOIN_HOST = 2;//申请加入擂主的请求信息
    public static final int CHALLENGE = 3;//挑战擂主的请求信息
    public static final int CHALLENGE_RSP = 4;//挑战擂主的请求信息
    public static final int UPDATE_HOST = 5;//更新擂主列表的信息
    public static final int PLAY = 6;//玩家下了一步棋的信息
    public static final int HUIQI = 8;//悔棋
    public static final int HUIQI_RSP = 9;
    public static final int SURRENDER = 10;//投降
    public static final int DOGFALL = 11;//平局
    public static final int DOGFALL_RSP = 12;//平局回应
    public static final int JOIN_COM = 15;//加入聊天室
    public static final int PUBLIC_CHAT = 16;//广播
    public static final int PRIVATE_CHAT = 17;//私聊
    public static final int UPDATE_COM = 20;//更新在线玩家的信息
    public static final int QUIT_COM=21;//退出聊天室
    public static final int QUIT_HOST=22;//退出擂台
    public static final int QUIT_GAME=23;//退出游戏
    public static final int SENDFILES=24;//发送图片请求
    public static final int SENDFILES_RSP=25;//图片回应
    public static final int GUIZHAN=26;//观战
    public static final int QUI_GUANZHAN=27;//退出观战
}
