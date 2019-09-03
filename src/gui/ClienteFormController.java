package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Cliente;
import model.exceptions.ValidationException;
import model.services.ClienteService;

public class ClienteFormController implements Initializable {
	
	private Cliente entity;
	
	private ClienteService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtEmpresa;

	@FXML
	private TextField txtProjeto;

	@FXML
	private Label labelErroE;
	
	@FXML
	private Label labelErroP;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;
	
	public void setCliente(Cliente entity) {
		this.entity = entity;
	}
	public void setClienteService(ClienteService service) {
		this.service = service;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("A entity estava nula!");
		}
		if(service == null) {
			throw new IllegalStateException("O Service estava nulo!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.atualStage(event).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Erro ao salvar o cliente", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}
	private Cliente getFormData() {
		Cliente obj = new Cliente();
		
		ValidationException exception = new ValidationException("Erro de validação");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtEmpresa.getText() == null || txtEmpresa.getText().trim().equals("")) {
			exception.addError("empresa", "O campo não pode ser vazio!");
		}
		obj.setEmpresa(txtEmpresa.getText());
		if(txtProjeto.getText() == null || txtProjeto.getText().trim().equals("")) {
			exception.addError("projeto", "O Campo não pode ser vazio!");
		}
		obj.setProjeto(txtProjeto.getText());
		
		if(exception.getErrors().size() > 0) {
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
		Constraints.setTextFieldMaxLength(txtEmpresa, 40);
		Constraints.setTextFieldMaxLength(txtProjeto, 40);
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("A entity está vazia");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtEmpresa.setText(entity.getEmpresa());
		txtProjeto.setText(entity.getProjeto());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("empresa")) {
			labelErroE.setText(errors.get("empresa"));
			if(fields.contains("projeto")) {
				labelErroP.setText(errors.get("projeto"));
			}
		}
	}

}
