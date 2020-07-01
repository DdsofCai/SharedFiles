package CurriculumDesign.Client03;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Chess5Utils {
	public static void showAlert(String msg){

		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.show();
	}
}
