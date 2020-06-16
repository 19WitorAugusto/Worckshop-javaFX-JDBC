package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Contraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label lblError;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;

	@FXML
	public void onBtnSaveAction() {
		System.out.println(" onBtnSaveAction!");
	}

	@FXML
	public void onBtnCancelAction() {
		System.out.println("onBtnSaveAction!");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	private void initializeNodes() {
		Contraints.setTextFieldInteger(txtId);
		Contraints.setTextFieldMaxLength(txtName, 30);
	}

}
