package br.com.deolho.listagem;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.deolho.br.com.modelo.Despesa;

/**
 * Created by Henrique on 01/09/2016.
 */
public class WebServiceConsumer extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://10.0.0.2:8080/DespesasParlamentaresWS/resources/email");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                StringBuilder stringBuilder1 = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    stringBuilder1.append(line);
                }

                List<Despesa> listaDespesas = retornaListaDespesas(stringBuilder1.toString());
                System.out.println("LISTA DO CARALEO DE DESPESAS = " + listaDespesas.size());

                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            System.out.println("erro = " + e.getMessage());
        } return true;
    }

    /** PROCESSA OS DADOS LIDOS DO JSON E CRIA OBJETOS DESPESA QUE SERÃO EXIBIDOS NO APP **/
    public List<Despesa> retornaListaDespesas(String jsonLido){

        List<Despesa> listaDespesas = new ArrayList<>();

        try {
            JSONArray jsonarray = new JSONArray(jsonLido);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                System.out.println("LENDO A LISTA!!");

                String ano = jsonobject.has("ano") ? jsonobject.getString("ano") : "2016";
                String mes = jsonobject.has("mes") ? jsonobject.getString("mes") : "01";
                String tipoParlamentar = jsonobject.has("tipoParlamentar") ? jsonobject.getString("tipoParlamentar") : "";
                String nome = jsonobject.has("nome") ? jsonobject.getString("nome") : "Não informado";
                String tipoDespesa = jsonobject.has("tipoDespesa") ? jsonobject.getString("tipoDespesa") : "Não informada";
                String cpfCnpj = jsonobject.has("cpfCnpj") ? jsonobject.getString("cpfCnpj") : "Não informado";
                String fornecedor = jsonobject.has("fornecedor") ? jsonobject.getString("fornecedor") : "Não informado";
                System.out.println(fornecedor);
                String documento = jsonobject.has("documento") ? jsonobject.getString("documento") : "Não informado";
                System.out.println(documento);
                String data = jsonobject.has("data") ? jsonobject.getString("data") : "Não informada";
                String descricaoDespesa = jsonobject.has("descricaoDespesa") ? jsonobject.getString("descricaoDespesa") : "Não informada";
                BigDecimal valor = new BigDecimal(jsonobject.getString("valor"));

                Despesa despesa = new Despesa(ano, mes, tipoParlamentar, nome, tipoDespesa, cpfCnpj,
                        fornecedor, documento, data, descricaoDespesa, valor);

                listaDespesas.add(despesa);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaDespesas;
    }
}