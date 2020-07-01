package CurriculumDesign.Client03;


import CurriculumDesign.Server.Player;

/**
 * @Author: 李检辉
 * @Date: 2019/8/20
 * @version V1.0
 * @Description:用于表示一场比赛：比方信息、棋盘数据、比赛结果
 * @Project: 网络编程技术
 * @Copyright: All rights reserved
 */
public class Competition {
	Player localPlayer, remotePlayer;
	int[][]data;
	int result;
	public Player getLocalPlayer() {
		return localPlayer;
	}
	public void setLocalPlayer(Player localPlayer) {
		this.localPlayer = localPlayer;
	}
	public Player getRemotePlayer() {
		return remotePlayer;
	}
	public void setRemotePlayer(Player remotePlayer) {
		this.remotePlayer = remotePlayer;
	}
	public int[][] getData() {
		return data;
	}
	public void setData(int[][] data) {
		this.data = data;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}

}
