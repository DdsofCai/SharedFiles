package CurriculumDesign.Client;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class Chess5Utils {
	public static void showAlert(String msg){

		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.show();
	}
}
