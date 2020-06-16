package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Contraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {

	private Department entity;//dependencia
	
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

	public void serDepartment(Department entity) {
		this.entity = entity;
	}
	
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
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		
			txtId.setText(String.valueOf(entity.getId()));
			txtName.setText(entity.getName());	
		
	}

}
