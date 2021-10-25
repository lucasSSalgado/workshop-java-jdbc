package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dcls = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	public void subscribeListener(DataChangeListener listener) {
		dcls.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormDate();

			service.saveOurUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMassage(e.getErros());

		} catch (DbException e) {
			Alerts.showAlert("Error saving obj", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener dcl : dcls) {
			dcl.onDataChanged();
		}
	}

	private Seller getFormDate() {
		Seller obj = new Seller();
		ValidationException Ve = new ValidationException("Validation Error");
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			Ve.addError("Name", "This field cannot be empty");
		}
		obj.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			Ve.addError("Email", "This field cannot be empty");
		}
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			Ve.addError("BirthDate", "This field cannot be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			Ve.addError("BaseSalary", "This field cannot be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());

		if (Ve.getErros().size() > 0) {
			throw Ve;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getDepartment() == null) {			
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		else {
		comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void setErrorMassage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		labelErrorName.setText((fields.contains("Name") ? errors.get("Name") : ""));
		labelErrorEmail.setText((fields.contains("Email") ? errors.get("Email") : ""));
		labelErrorBirthDate.setText((fields.contains("BirthDate") ? errors.get("BirthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("BaseSalary") ? errors.get("BaseSalary") : ""));

	}

	public void loadAssocietedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Department Service was null");
		} 
		else {
			List<Department> list = departmentService.findAll();
			obsList = FXCollections.observableArrayList(list);
			comboBoxDepartment.setItems(obsList);
		}
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
