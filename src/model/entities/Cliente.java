package model.entities;

import java.io.Serializable;

public class Cliente implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String empresa;
	private String projeto;
	
	public Cliente() {
	}

	public Cliente(Integer id, String empresa, String projeto) {
		this.id = id;
		this.empresa = empresa;
		this.projeto = projeto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getProjeto() {
		return projeto;
	}

	public void setProjeto(String projeto) {
		this.projeto = projeto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", empresa=" + empresa + ", projeto=" + projeto + "]";
	}
	
	

}
