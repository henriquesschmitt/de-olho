package br.com.deolho.modelo;

import java.math.BigDecimal;

public class Despesa {
    
    private int idDespesa;	
    private String ano;
    private String mes;
    private String tipoParlamentar;
    private String nome;
    private String tipoDespesa;
    private String cpfCnpj;
    private String fornecedor;
    private String documento;
    private String data;
    private String descricaoDespesa;
    private BigDecimal valor;

    public Despesa(String ano, String mes, String tipoParlamentar, String nome, String tipoDespesa, String cpfCnpj, 
    		String fornecedor, String documento, String data, String descricaoDespesa, BigDecimal valor) {
        this.ano = ano;
        this.mes = mes;
        this.tipoParlamentar = tipoParlamentar;
        this.nome = nome;
        this.tipoDespesa = tipoDespesa;
        this.cpfCnpj = cpfCnpj;
        this.fornecedor = fornecedor;
        this.documento = documento;
        this.data = data;
        this.descricaoDespesa = descricaoDespesa;
        this.valor = valor;
    }
    
    public Despesa(){}

    /**
     * @return the ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * @param ano the ano to set
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * @return the mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * @return the tipoParlamentar
     */
    public String getTipoParlamentar() {
        return tipoParlamentar;
    }

    /**
     * @param tipoParlamentar the tipoParlamentar to set
     */
    public void setTipoParlamentar(String tipoParlamentar) {
        this.tipoParlamentar = tipoParlamentar;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the tipoDespesa
     */
    public String getTipoDespesa() {
        return tipoDespesa;
    }

    /**
     * @param tipoDespesa the tipoDespesa to set
     */
    public void setTipoDespesa(String tipoDespesa) {
        this.tipoDespesa = tipoDespesa;
    }

    /**
     * @return the cpfCnpj
     */
    public String getCpfCnpj() {
        return cpfCnpj;
    }

    /**
     * @param cpfCnpj the cpfCnpj to set
     */
    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    /**
     * @return the fornecedor
     */
    public String getFornecedor() {
        return fornecedor;
    }

    /**
     * @param fornecedor the fornecedor to set
     */
    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    /**
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return the descricaoDespesa
     */
    public String getDescricaoDespesa() {
        return descricaoDespesa;
    }

    /**
     * @param descricaoDespesa the descricaoDespesa to set
     */
    public void setDescricaoDespesa(String descricaoDespesa) {
        this.descricaoDespesa = descricaoDespesa;
    }

    /**
     * @return the valor
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

	private int getIdDespesa() {
		return idDespesa;
	}

	private void setIdDespesa(int idDespesa) {
		this.idDespesa = idDespesa;
	}
    
    
    
}
