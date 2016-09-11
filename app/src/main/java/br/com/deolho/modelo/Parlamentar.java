package br.com.deolho.modelo;

/**
 * Created by dell on 09/09/2016.
 */
public class Parlamentar {

    private String codigo;
    private String nome;
    private String sexo;
    private String cargo;
    private String urlFoto;
    private String partido;
    private String gastoTotal;
    private String gastoDia;

    public Parlamentar(String codigo, String nome, String sexo, String cargo, String urlFoto, String partido, String gastoTotal, String gastoDia) {
        this.codigo = codigo;
        this.nome = nome;
        this.sexo = sexo;
        this.cargo = cargo;
        this.urlFoto = urlFoto;
        this.partido = partido;
        this.gastoTotal = gastoTotal;
        this.gastoDia = gastoDia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(String gastoTotal) {
        this.gastoTotal = gastoTotal;
    }

    public String getGastoDia() {
        return gastoDia;
    }

    public void setGastoDia(String gastoDia) {
        this.gastoDia = gastoDia;
    }
}
