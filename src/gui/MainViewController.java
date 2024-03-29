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
import model.services.ClienteService;
import model.services.FuncionarioService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemFuncionario;

	@FXML
	private MenuItem menuItemCliente;

	@FXML
	private MenuItem menuItemSobre;

	@FXML
	private void onMenuItemFuncionarioAction() {
		loadView("/gui/FuncionarioList.fxml", (FuncionarioListController controller) -> {
			controller.setFuncionarioService(new FuncionarioService());
			controller.updateTableView();
			
		});
	}

	@FXML
	private void onMenuItemClienteAction() {
		loadView("/gui/ClienteList.fxml", (ClienteListController controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
			
		});
	}

	@FXML
	private void onMenuItemSobreAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
			
		} 
		catch (IOException e) {
			Alerts.showAlert("IO EXception", "Erro carregando a pagina", e.getMessage(), AlertType.ERROR);
		}
	}

}
