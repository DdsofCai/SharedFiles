package CurriculumDesign.Server;

import java.io.Serializable;
/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */

public class Point implements Serializable{
	
	public int column, row,action;
	public int color;
	
	public Point(int column, int row, int color, int action){
		this.column = column;
		this.row = row;
		this.color = color;
		this.action = action;
	}
	public String toString(){
		return "[" + this.column + "," + row + "," + color + "," + action + "]";
	}
	
}
