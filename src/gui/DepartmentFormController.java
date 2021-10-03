package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentSevice;

public class DepartmentFormController implements Initializable{
	
	private Department entity;
	
	private DepartmentSevice service;
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service ==null) {
			throw new IllegalStateException("Service was null");
		}
		try {
		entity = getFormDate();
		service.saveOurUpdate(entity);
		Utils.currentStage(event).close();
		}
		catch (DbException e){
			Alerts.showAlert("Error saving obj", null, e.getMessage(), AlertType.ERROR);
			
		}
	}
	
	private Department getFormDate() {
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void setDepartment(Department entity) {
		this.entity = entity;
		}
	
	public void setDepartmentService(DepartmentSevice service) {
		this.service = service;
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

}
