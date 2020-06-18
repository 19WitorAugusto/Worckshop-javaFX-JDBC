package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;// dependencia

	@FXML
	private TableView<Seller> TableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> TableColumnID;
	@FXML
	private TableColumn<Seller, String> TableColumnNome;
	@FXML
	private Button btnNew;
	@FXML
	private TableColumn<Seller, String> TableColumnEmail;
	@FXML
	private TableColumn<Seller, Date> TableColumnBirthData;
	
	@FXML
	private TableColumn<Seller, Double> TableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	private ObservableList<Seller> ObsList;

	@FXML
	public void onBtnNewAction(ActionEvent event) {

		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();

		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	public void setSellerService(SellerService service) {
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
		
		TableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		TableColumnBirthData.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(TableColumnBirthData, "dd/MM/yyyy");
		TableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(TableColumnBaseSalary, 2);

		// tabela acompanhar a tela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		TableViewSeller.prefHeightProperty().bind(stage.heightProperty());// marcete
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service estava nulo!");
		}
		List<Seller> list = service.findAll();
		ObsList = FXCollections.observableArrayList(list);
		TableViewSeller.setItems(ObsList);
		initEditButtons();
		initRemoveButtons();

	}

	public void createDialogForm(Seller obj, String absolutName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			// referencia para o controlador
			SellerFormController controller = loader.getController();
			controller.setSeller(obj);
			controller.setSellerService(new SellerService());
			// at. tabela
			controller.subscribeDataChangeListener(this);// me inscrevendo

			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do vendedor");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

	private void removeEntity(Seller obj) {
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
