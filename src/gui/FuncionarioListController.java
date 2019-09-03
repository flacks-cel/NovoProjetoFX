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
import model.entities.Funcionario;
import model.services.FuncionarioService;

public class FuncionarioListController implements Initializable, DataChangeListener {

	private FuncionarioService service;

	@FXML
	private TableView<Funcionario> tableViewFuncionario;

	@FXML
	private TableColumn<Funcionario, Integer> tableColumnId;

	@FXML
	private TableColumn<Funcionario, String> tableColumnNome;

	@FXML
	private TableColumn<Funcionario, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Funcionario, Date> tableColumnInicio;
	
	@FXML
	private TableColumn<Funcionario, Double> tableColumnSalario;

	@FXML
	private TableColumn<Funcionario, Funcionario> tableColumnEDIT;

	@FXML
	private TableColumn<Funcionario, Funcionario> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	public void setFuncionarioService(FuncionarioService service) {
		this.service = service;
	}

	@FXML
	public void onBtNovoAction(ActionEvent evento) {
		Stage parentStage = Utils.atualStage(evento);
		Funcionario obj = new Funcionario();
		criaDialogoFormu(obj, "/gui/FuncionarioForm.fxml", parentStage);
	}

	private ObservableList<Funcionario> obsList;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializaNode();

	}

	private void inicializaNode() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnInicio.setCellValueFactory(new PropertyValueFactory<>("inicio"));
		Utils.formatTableColumnDate(tableColumnInicio, "dd/MM/yyyy");
		tableColumnSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
		Utils.formatTableColumnDouble(tableColumnSalario, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewFuncionario.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service estava nulo!");
		}

		List<Funcionario> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewFuncionario.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void criaDialogoFormu(Funcionario obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FuncionarioFormController controller = loader.getController();
			controller.setFuncionario(obj);
			controller.setFuncionarioService(new FuncionarioService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogoStage = new Stage();
			dialogoStage.setTitle("Insira os dados do cliente");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Funcionario, Funcionario>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Funcionario obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(event -> criaDialogoFormu(obj, "/gui/FuncionarioForm.fxml", Utils.atualStage(event)));

			}

		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Funcionario, Funcionario>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Funcionario obj, boolean empty) {
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

	private void removeEntity(Funcionario obj) {
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
