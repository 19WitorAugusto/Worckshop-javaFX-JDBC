package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class mainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbount;

	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();

		});
	}

	@FXML
	public void onMenuItemAbount() {
		loadView("/gui/Abount.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	// para nao ser interrompido => synchronized
	private synchronized <T> void loadView(String absoluteName, Consumer<T> acaoInicializacao) {
		try {
			// carregar tela(View)
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();

			// mostra view na janela principal
			Scene mainScene = Main.getMainScene();// referencia da cena
			// getRoot.. pegar o primeiro elemento da view- primeiro é o ScrollPane.
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();// pega referencia para um vbox que
																					// esta na janela principal.

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();// limpar todos os filhos do main VBox
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());

			T controller = loader.getController();
			acaoInicializacao.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "erro carregando a página", e.getMessage(), AlertType.ERROR);
		}

	}

}
