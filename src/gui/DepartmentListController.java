package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;// dependencia

	@FXML
	private TableView<Department> TableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> TableColumnID;
	@FXML
	private TableColumn<Department, String> TableColumnNome;
	@FXML
	private Button btnNew;
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	private ObservableList<Department> ObsList;

	@FXML
	public void onBtnNewAction(ActionEvent event) {

		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();

		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	// iniciar o comportamento das colunas na tabela
	private void initializeNodes() {
		TableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumnNome.setCellValueFactory(new PropertyValueFactory<>("name"));

		// tabela acompanhar a tela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		TableViewDepartment.prefHeightProperty().bind(stage.heightProperty());// marcete
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service estava nulo!");
		}
		List<Department> list = service.findAll();
		ObsList = FXCollections.observableArrayList(list);
		TableViewDepartment.setItems(ObsList);
		initEditButtons();
		initRemoveButtons();

	}

	public void createDialogForm(Department obj, String absolutName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			// referencia para o controlador
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			// at. tabela
			controller.subscribeDataChangeListener(this);// me inscrevendo

			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);// estado pai
			dialogStage.initModality(Modality.WINDOW_MODAL);// travada enquanto estiver aberta
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO EXCEPTIONS", "Error loading view", e.getMessage(), AlertType.ERROR);

		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja deletar?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}

		}

	}

}
