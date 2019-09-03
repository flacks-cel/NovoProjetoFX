package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Funcionario;
import model.exceptions.ValidationException;
import model.services.FuncionarioService;

public class FuncionarioFormController implements Initializable {

	private Funcionario entity;

	private FuncionarioService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker diInicio;

	@FXML
	private TextField txtSalario;

	@FXML
	private Label labelErroN;

	@FXML
	private Label labelErroM;

	@FXML
	private Label labelErroI;

	@FXML
	private Label labelErroS;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setFuncionario(Funcionario entity) {
		this.entity = entity;
	}

	public void setFuncionarioService(FuncionarioService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (entity == null) {
			throw new IllegalStateException("A entity estava nula!");
		}
		if (service == null) {
			throw new IllegalStateException("O Service estava nulo!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.atualStage(evento).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar o cliente", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Funcionario getFormData() {
		Funcionario obj = new Funcionario();

		ValidationException exception = new ValidationException("Erro de validação");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "O campo não pode ser vazio!");
		}
		obj.setNome(txtNome.getText());
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "O Campo não pode ser vazio!");
		}
		obj.setEmail(txtEmail.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.atualStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldDouble(txtSalario);
		Utils.formatDatePicker(diInicio, "dd/MM/yyyy");
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("A entity está vazia");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entity.getSalario()));
		if (entity.getInicio() != null) {
			diInicio.setValue(LocalDate.ofInstant(entity.getInicio().toInstant(), ZoneId.systemDefault()));
		}
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("nome")) {
			labelErroN.setText(errors.get("nome"));
			if (fields.contains("email")) {
				labelErroM.setText(errors.get("email"));
			}
		}
	}

}
