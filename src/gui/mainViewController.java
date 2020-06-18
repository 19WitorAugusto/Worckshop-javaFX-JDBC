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

public class MainViewController implements Initializable {

	// atributos dos itens de menu
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

	// metodos para tratar os eventos do menu
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAbount() {
		loadView("/gui/About.fxml", x -> {
		});
	}

	// metodo da interface initialize
	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	// carregar tela
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			// mostrar view dentro da tela principal
			Scene mainScene = Main.getMainScene();// referencia da cena
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0); // primeiro filho da janela principal
			mainVBox.getChildren().clear();// limpar todos os filhos do meu mainVbox.
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());

			// ativar função que é passada pelo segundo parametro

			T controller = loader.getController();
			initializingAction.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro carregando pagina", e.getMessage(), AlertType.ERROR);
		}

	}

	/*
	 * private synchronized void loadView2(String absoluteName) { try { FXMLLoader
	 * loader = new FXMLLoader(getClass().getResource(absoluteName)); VBox newVBox =
	 * loader.load(); // mostrar view dentro da tela principal Scene mainScene =
	 * Main.getMainScene();// referencia da cena VBox mainVBox = (VBox)
	 * ((ScrollPane) mainScene.getRoot()).getContent();
	 * 
	 * Node mainMenu = mainVBox.getChildren().get(0); // primeiro filho da janela
	 * principal mainVBox.getChildren().clear();// limpar todos os filhos do meu
	 * mainVbox. mainVBox.getChildren().add(mainMenu);
	 * mainVBox.getChildren().addAll(newVBox.getChildren());
	 * 
	 * DepartmentListController controller = loader.getController();
	 * controller.setDepartmentService(new DepartmentService());
	 * controller.updateTableView();
	 * 
	 * } catch (IOException e) { Alerts.showAlert("IO Exception",
	 * "Erro carregando pagina", e.getMessage(), AlertType.ERROR); }
	 * 
	 * }
	 */
}
