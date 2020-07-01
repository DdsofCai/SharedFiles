package CurriculumDesign.Client03;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author: 李检辉
 * @Date: 2019/8/20
 * @version V1.0
 * @Description:用于定义客户端要用到的一些常量
 * @Project: 网络编程技术
 * @Copyright: All rights reserved
 */

public class ClientConfig {
	//服务器的地址
	public static final SocketAddress SERVER_ADDR 
			= new InetSocketAddress("172.16.134.2", 6666);
	//本地的地址
	public static final SocketAddress LOCAL_ADDR 
			= new InetSocketAddress("172.16.134.2", 3333);
	
	public static final int ACTION_TYPE_ADD = 1;//新下棋
	public static final int ACTION_TYPE_DEL = 0;//悔棋
	public static final int COLOR_WHITE=2;
	public static final int COLOR_BLACK=1;
	public static final int COLOR_BLANK=0;
	
}
