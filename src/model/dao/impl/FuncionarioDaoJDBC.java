package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.FuncionarioDao;
import model.entities.Cliente;
import model.entities.Funcionario;

public class FuncionarioDaoJDBC implements FuncionarioDao {

	private Connection conn;

	public FuncionarioDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Funcionario cli) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO funcionario "
					+ "(Nome, Email, Inicio, Salario, ClienteId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS );
			
			st.setString(1, cli.getNome());
			st.setString(2, cli.getEmail());
			st.setDate(3, new java.sql.Date(cli.getInicio().getTime()));
			st.setDouble(4, cli.getSalario());
			st.setInt(5, cli.getCliente().getId());
			
			int linhaAfetada = st.executeUpdate();
			
			if (linhaAfetada > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					cli.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Erro inesperado, nenhuma linha foi afetada !");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Funcionario cli) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE funcionario "
					+ "SET Nome = ?, Email = ?, Inicio = ?, Salario = ?, ClienteId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, cli.getNome());
			st.setString(2, cli.getEmail());
			st.setDate(3, new java.sql.Date(cli.getInicio().getTime()));
			st.setDouble(4, cli.getSalario());
			st.setInt(5, cli.getCliente().getId());
			st.setInt(6, cli.getId());
			
			st.executeUpdate();
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			
			st = conn.prepareStatement("DELETE FROM funcionario WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
			
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Funcionario findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
		st = conn.prepareStatement(
				"SELECT funcionario.*,Empresa as EmpNome,Projeto as EmpProj "
				+ "FROM funcionario INNER JOIN cliente "
				+ "ON funcionario.ClienteId = cliente.Id "
				+ "WHERE funcionario.Id = ?");
		
		st.setInt(1, id);
		rs = st.executeQuery();
		if(rs.next()) {
			Cliente cli = instantiateCliente(rs);
			Funcionario obj = instantiateFuncionario(rs, cli);
			return obj;
		}
		return null;
		}
		catch(SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Funcionario instantiateFuncionario(ResultSet rs, Cliente cli) throws SQLException {
		Funcionario obj = new Funcionario();
		obj.setId(rs.getInt("Id"));
		obj.setNome(rs.getString("Nome"));
		obj.setEmail(rs.getString("Email"));
		obj.setInicio(new java.util.Date(rs.getTimestamp("iNICIO").getTime()));
		obj.setSalario(rs.getDouble("Salario"));
		obj.setCliente(cli);
		return obj;
	}

	private Cliente instantiateCliente(ResultSet rs) throws SQLException {
		Cliente cli = new Cliente();
		cli.setId(rs.getInt("ClienteId"));
		cli.setEmpresa(rs.getString("EmpNome"));
		cli.setProjeto(rs.getString("EmpProj"));
		return cli;
	}

	@Override
	public List<Funcionario> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
		st = conn.prepareStatement(
				"SELECT funcionario.*,cliente.Empresa as EmpNome, cliente.Projeto as EmpProj "
				+ "FROM funcionario INNER JOIN cliente "
				+ "ON funcionario.ClienteId = cliente.Id "
				+ "ORDER BY Empresa");
		
		rs = st.executeQuery();
		
		List<Funcionario> list = new ArrayList<>();
		Map<Integer, Cliente> map = new HashMap<>();
		
		while(rs.next()) {
			
			Cliente cli = map.get(rs.getInt("ClienteId"));
			
			if(cli == null) {
				cli = instantiateCliente(rs);
				map.put(rs.getInt("ClienteId"), cli);
			}
			
			Funcionario obj = instantiateFuncionario(rs, cli);
			list.add(obj);
		}
		return list;
		}
		catch(SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
		public List<Funcionario> findByCliente(Cliente cliente) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
		st = conn.prepareStatement(
				"SELECT funcionario.*,cliente.Empresa as EmpNome, cliente.Projeto as EmpProj "
				+ "FROM funcionario INNER JOIN cliente "
				+ "ON funcionario.ClienteId = cliente.Id "
				+ "WHERE ClienteId = ? "
				+ "ORDER BY Empresa");
		
		st.setInt(1, cliente.getId());
		
		rs = st.executeQuery();
		
		List<Funcionario> list = new ArrayList<>();
		Map<Integer, Cliente> map = new HashMap<>();
		
		while(rs.next()) {
			
			Cliente cli = map.get(rs.getInt("ClienteId"));
			
			if(cli == null) {
				cli = instantiateCliente(rs);
				map.put(rs.getInt("ClienteId"), cli);
			}
			
			Funcionario obj = instantiateFuncionario(rs, cli);
			list.add(obj);
		}
		return list;
		}
		catch(SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}
}
