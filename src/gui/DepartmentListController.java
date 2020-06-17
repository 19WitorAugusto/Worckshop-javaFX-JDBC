package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
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

	private DepartmentService service;// declarando dependencia;

	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColunmId;
	@FXML
	private TableColumn<Department, String> tableColunmName;
	@FXML
	private Button btNew;
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	private void initializeNodes() {
		tableColunmId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColunmName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// para a table view acompanhar a janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		// responsavel por acessar os serviços e carregar os departamentos
		// e tambem joga-los na ObservableList.
		if (service == null) {
			throw new IllegalStateException("Sercive was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChengeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);// nao pode ser redimencionada
			dialogStage.initOwner(parentStage);// quem é o stage pai da janela.
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);

		}
	}

	@Override
	public void onDataChange() {
		updateTableView();

	}

	private void initEditButtons() {// função para colocar botao de edição em todos da tabela
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

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
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "TEM CERTEZA QUE VOCÊ DESEJA DELETAR? ");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
