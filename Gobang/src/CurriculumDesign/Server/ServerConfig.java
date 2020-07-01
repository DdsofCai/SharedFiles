package CurriculumDesign.Server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */

public class ServerConfig {
	//服务器开启的UDP端口
	public final static SocketAddress SERVER_ADDR = new InetSocketAddress("192.168.124.27", 8888);
	//public final static int CLIENT_LISTEN_PORT_DEFAULT = 5000;
	private static HashMap<String, Player> hostMap = new HashMap<String, Player>();//用于存放擂主信息
	private static HashMap<String, Player> playerMap = new HashMap<String, Player>();//用于存放所有玩家的信息
	private static HashMap<String,Player> playersComList=new HashMap<String, Player>();//用于存在所有在线玩家的信息
	private static HashMap<String,Player> playingMap = new HashMap<String,Player>();//用于存放正在比赛的玩家
	private static HashMap<String,Player> guanZhanMap = new HashMap<String,Player>();//存放观战的玩家
	public static void addHost(String name, Player player){
		hostMap.put(name, player);
	}//添加擂主
	public static void addComPlayer(String name ,Player player ){playersComList.put(name,player);}//添加聊天室在线玩家
	public static void addPlayingPlayer(String name ,Player player ){playingMap.put(name,player);}//添加游戏正在对战玩家

	public static boolean containHost(String name){
		return hostMap.containsKey(name);
	}//判断擂主是否存在
	public static boolean containPlaying(String name){
		return playingMap.containsKey(name);
	}//判断擂主是否存在
	public static boolean containPlayer(String name){
		return playerMap.containsKey(name);
	}//判断玩家是否存在

	public static HashMap<String,Player> getHostMap(){return hostMap;}//获取擂主Map
	public static HashMap<String, Player> getPlayerMap(){
		return playerMap;
	}//获取玩家Map
	public static HashMap<String,Player> getPlayerComMap(){return playersComList;}//获取聊天室玩家Map
	public static HashMap<String,Player> getGuanZhanMap(){return guanZhanMap;}//获取观战Map

	public static Player getPlayer(String name){
		return playerMap.get(name);
	}//获取玩家
	public static Player getComPlayer(String name){return playersComList.get(name);}//获取聊天室玩家
	public static Player getGuanZhan(String name){return guanZhanMap.get(name);}//获取观战玩家

	public static void addPlaying(String name, Player player){
		playingMap.put(name, player);
	}//添加在线玩家
	public static void addPlayer(String name, Player player){
		playerMap.put(name, player);
	}//添加玩家
	public static void addGuanZhanPlayer(String name,Player player){guanZhanMap.put(name,player);}//添加观战玩家

	public static void delComPlayer(String name){
		playersComList.remove(name);
	}//删除聊天室玩家
	public static void delHostPlayer(String name){ hostMap.remove(name); }//删除擂主
	public static void delPlaying(String name){ playingMap.remove(name); }//删除在线玩家
	public static void delMapPlayer(String name){playerMap.remove(name);}//删除玩家
	public static void delGuanZhan(String name){guanZhanMap.remove(name);}//删除观战玩家
	public static void delAllPlayer(String name){
		delGuanZhan(name);
		delMapPlayer(name);
		delHostPlayer(name);
		delComPlayer(name);
		delPlaying(name);
	}
	//获取玩家地址
	public static SocketAddress getPlayerAddr(String name){ return getPlayer(name).getAddress(); }
	//获取聊天室玩家列表
	public static  String getComNamesList(){
		String str = "";
		Iterator<String> it = playersComList.keySet().iterator();
		while(it.hasNext()){
			str = str  + it.next() + ":";
		}
		System.out.println("get:"+str);
		return str;
	}
	//获取擂主列表
	public static String getHostNamesList(){
		String str = "";	
		Iterator<String> it = hostMap.keySet().iterator();	
		while(it.hasNext()){
			str = str  + it.next() + ":";		
		}
		System.out.println("get:"+str);
		return str;
	}
}
