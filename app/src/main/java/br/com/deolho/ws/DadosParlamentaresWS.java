package br.com.deolho.ws;

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
import java.util.ArrayList;
import java.util.List;

import br.com.deolho.modelo.Despesa;
import br.com.deolho.modelo.Parlamentar;

/**
 * Created by Henrique on 01/09/2016.
 */
public class DadosParlamentaresWS extends AsyncTask<String, Void, List<Parlamentar>> {

    @Override
    protected List<Parlamentar> doInBackground(String... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://meucongressonacional.com/api/001/senador");
        List<Parlamentar> listaDadosParlamentares = new ArrayList<>();

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

                listaDadosParlamentares = retornaListaDadosParlamentares(stringBuilder1.toString());

                inputStream.close();

            } else {
                Log.d("JSON", "Failed to download file");
            }

        } catch (Exception e) {
            System.out.println("erro = " + e.getMessage());
        }
        return listaDadosParlamentares;
    }

    public List<Parlamentar> retornaListaDadosParlamentares(String jsonLido){

        List<Parlamentar> listaDados = new ArrayList<>();

        try {
            JSONArray jsonarray = new JSONArray(jsonLido);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                System.out.println("LENDO A LISTA!!");

                String codigo = jsonobject.getString("id");
                String nome =  jsonobject.getString("nomeCompleto");
                String sexo = jsonobject.getString("sexo");
                String cargo = jsonobject.getString("cargo");
                String urlFoto = jsonobject.getString("fotoURL");
                String partido = jsonobject.getString("partido");
                String gastoTotal = jsonobject.getString("gastoTotal");
                String gastoDia = jsonobject.getString("gastoPorDia");

                Parlamentar parlamentar = new Parlamentar(codigo, nome, sexo, cargo, urlFoto, partido, gastoTotal, gastoDia);
                listaDados.add(parlamentar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}