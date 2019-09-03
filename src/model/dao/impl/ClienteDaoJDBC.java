package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.ClienteDao;
import model.entities.Cliente;

public class ClienteDaoJDBC implements ClienteDao {

	private Connection conn;

	public ClienteDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Cliente cli) {

		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO cliente " + "(Empresa, Projeto) " + "VALUES " + "(?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, cli.getEmpresa());
			st.setString(2, cli.getProjeto());

			int linhaAfetada = st.executeUpdate();

			if (linhaAfetada > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					cli.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro inesperado, nenhuma linha foi afetada !");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Cliente cli) {

		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE cliente " + "SET Empresa = ?, Projeto = ? " + "WHERE Id = ?");

			st.setString(1, cli.getEmpresa());
			st.setString(2, cli.getProjeto());
			st.setInt(3, cli.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {

		PreparedStatement st = null;
		try {

			st = conn.prepareStatement("DELETE FROM cliente WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Cliente findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM cliente WHERE Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				
				Cliente cli = new Cliente();
				cli.setId(rs.getInt("Id"));
				cli.setEmpresa(rs.getString("Empresa"));
				cli.setProjeto(rs.getString("Projeto"));
				return cli;
			}
			
			return null;
			
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		} 
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public List<Cliente> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM cliente ORDER BY Empresa");

			rs = st.executeQuery();

			List<Cliente> list = new ArrayList<>();

			while (rs.next()) {

				Cliente cli = new Cliente();
				cli.setId(rs.getInt("Id"));
				cli.setEmpresa(rs.getString("Empresa"));
				cli.setProjeto(rs.getString("Projeto"));
				list.add(cli);

			}
			return list;
		}

		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
