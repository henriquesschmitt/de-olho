package br.com.deolho.ws;

import android.app.ProgressDialog;
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
import java.util.ArrayList;
import java.util.List;

import br.com.deolho.deolho.MainActivity;
import br.com.deolho.modelo.Despesa;
import br.com.deolho.modelo.Parlamentar;

/**
 * Created by dell on 10/09/2016.
 */
public class DespesasParlamentaresWS {

    public static List<Despesa> listaDadosParlamentares = new ArrayList<>();

    public List<Despesa> getDespesaParlamentares(final String url){

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(url);

                        try {
                            HttpResponse response = httpClient.execute(httpGet);
                            StatusLine statusLine = response.getStatusLine();
                            int statusCode = statusLine.getStatusCode();
                            String line = null;
                            StringBuilder stringBuilder1 = new StringBuilder();

                            if (statusCode == 200) {
                                HttpEntity entity = response.getEntity();
                                InputStream inputStream = entity.getContent();
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(inputStream));

                                while ((line = reader.readLine()) != null) {
                                    stringBuilder1.append(line);
                                }

                                listaDadosParlamentares = retornaListaDespesas(stringBuilder1.toString());

                                //FECHA O PROGRESS DIALOG ABERTO
                                MainActivity.pd.dismiss();

                                inputStream.close();

                            } else {
                                Log.d("JSON", "Failed to download file");
                            }

                        } catch (Exception e) {
                            System.out.println("erro = " + e.getMessage());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return listaDadosParlamentares;
    }

    /** PROCESSA OS DADOS LIDOS DO JSON E CRIA OBJETOS DESPESA QUE SERÃO EXIBIDOS NO APP **/
    public List<Despesa> retornaListaDespesas(String jsonLido){

        List<Despesa> listaDespesas = new ArrayList<>();

        try {
            JSONArray jsonarray = new JSONArray(jsonLido);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String ano = jsonobject.has("ano") ? jsonobject.getString("ano") : "2016";
                String mes = jsonobject.has("mes") ? jsonobject.getString("mes") : "01";
                String tipoParlamentar = jsonobject.has("tipoParlamentar") ? jsonobject.getString("tipoParlamentar") : "";
                String nome = jsonobject.has("nome") ? jsonobject.getString("nome") : "Não informado";
                String tipoDespesa = jsonobject.has("tipoDespesa") ? jsonobject.getString("tipoDespesa") : "Não informada";
                String cpfCnpj = jsonobject.has("cpfCnpj") ? jsonobject.getString("cpfCnpj") : "Não informado";
                String fornecedor = jsonobject.has("fornecedor") ? jsonobject.getString("fornecedor") : "Não informado";
                String documento = jsonobject.has("documento") ? jsonobject.getString("documento") : "Não informado";
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
