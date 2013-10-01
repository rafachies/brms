package org.rchies.brms.sample.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Transacao implements Serializable {

	private static final long serialVersionUID = -1558059494459735310L;

	private Integer id; 
	private String bandeira;
	private String tipo;
	private String moeda;
	private Date data;
	private Integer parcelas;
	private Integer codigoNegativa;
	private boolean checkFraude;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBandeira() {
		return bandeira;
	}
	public void setBandeira(String bandeira) {
		this.bandeira = bandeira;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getMoeda() {
		return moeda;
	}
	public void setMoeda(String moeda) {
		this.moeda = moeda;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Integer getParcelas() {
		return parcelas;
	}
	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}
	public Integer getCodigoNegativa() {
		return codigoNegativa;
	}
	public void setCodigoNegativa(Integer codigoNegativa) {
		this.codigoNegativa = codigoNegativa;
	}
	public boolean isCheckFraude() {
		return checkFraude;
	}
	public void setCheckFraude(boolean checkFraude) {
		this.checkFraude = checkFraude;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false;}
		Transacao other = (Transacao) obj;
		return new EqualsBuilder()
			.appendSuper(super.equals(other))
			.append(this.id, other.id)
			.append(this.bandeira, other.bandeira)
			.append(this.tipo, other.tipo)
			.append(this.data, other.data)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(super.hashCode())
			.append(id)
			.append(bandeira)
			.append(tipo)
			.append(data)
			.hashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(id)
			.append(bandeira)
			.append(tipo)
			.append(data)
			.toString();
	}
}
