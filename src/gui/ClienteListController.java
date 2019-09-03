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
import model.entities.Cliente;
import model.services.ClienteService;

public class ClienteListController implements Initializable, DataChangeListener {

	private ClienteService service;

	@FXML
	private TableView<Cliente> tableViewCliente;

	@FXML
	private TableColumn<Cliente, Integer> tableColumnId;

	@FXML
	private TableColumn<Cliente, String> tableColumnEmpresa;

	@FXML
	private TableColumn<Cliente, String> tableColumnProjeto;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnEDIT;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	public void setClienteService(ClienteService service) {
		this.service = service;
	}

	@FXML
	public void onBtNovoAction(ActionEvent evento) {
		Stage parentStage = Utils.atualStage(evento);
		Cliente obj = new Cliente();
		criaDialogoFormu(obj, "/gui/ClienteForm.fxml", parentStage);
	}

	private ObservableList<Cliente> obsList;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializaNode();

	}

	private void inicializaNode() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
		tableColumnProjeto.setCellValueFactory(new PropertyValueFactory<>("projeto"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service estava nulo!");
		}

		List<Cliente> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void criaDialogoFormu(Cliente obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ClienteFormController controller = loader.getController();
			controller.setCliente(obj);
			controller.setClienteService(new ClienteService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogoStage = new Stage();
			dialogoStage.setTitle("Insira a empresa e o projeto");
			dialogoStage.setScene(new Scene(pane));
			dialogoStage.setResizable(false);
			dialogoStage.initOwner(parentStage);
			dialogoStage.initModality(Modality.WINDOW_MODAL);
			dialogoStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar a visualização", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(event -> criaDialogoFormu(obj, "/gui/ClienteForm.fxml", Utils.atualStage(event)));

			}

		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
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

	private void removeEntity(Cliente obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que quer deletar?");
		
		if(result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("service estava nulo!");
			}
			try {
				service.remove(obj);
				updateTableView();
		}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover", null, e.getMessage(), AlertType.ERROR);
			}
	}

	}
}
