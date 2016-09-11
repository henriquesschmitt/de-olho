package br.com.deolho.deolho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import br.com.deolho.modelo.Parlamentar;
import br.com.deolho.ws.DadosParlamentaresWS;

public class FilterActivity extends AppCompatActivity {

    private ProgressDialog pd;
    public static List<Parlamentar> listaDadosParlamentares = null;
    Spinner sParlamentares;
    Spinner sPartidos;
    List<String> spinnerPartidosArray =  new ArrayList<String>();
    List<String> spinnerParlamentaresArray =  new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        /** TRAZ DADOS DE PARLAMENTARES **/
        getDadosParlamentares("http://meucongressonacional.com/api/001/senador");
        pd.dismiss();
        getDadosParlamentares("http://meucongressonacional.com/api/001/deputado");
        pd.dismiss();

        spinnerPartidosArray.add("Selecione o partido");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerPartidosArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPartidos = (Spinner) findViewById(R.id.spinnerPartido);
        sPartidos.setAdapter(adapter);

        //----------------------

        spinnerParlamentaresArray.add("Selecione o parlamentar");
        ArrayAdapter<String> adapterParlamentar = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerParlamentaresArray);
        adapterParlamentar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sParlamentares = (Spinner) findViewById(R.id.spinnerParlamentar);
        sParlamentares.setAdapter(adapterParlamentar);

    }

    public void getDadosParlamentares(final String url){

        pd = ProgressDialog.show(this, "Aguarde",
                "Buscando dados de parlamentares...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public List<Parlamentar> retornaListaDadosParlamentares(String jsonLido){

        List<Parlamentar> listaDados = new ArrayList<>();

        try {
            JSONArray jsonarray = new JSONArray(jsonLido);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String codigo = jsonobject.getString("id");
                String nome =  jsonobject.getString("nomeCompleto");
                String sexo = jsonobject.getString("sexo");
                String cargo = jsonobject.getString("cargo");
                String urlFoto = jsonobject.getString("fotoURL");
                String partido = jsonobject.getString("partido");
                String gastoTotal = jsonobject.getString("gastoTotal");
                String gastoDia = jsonobject.getString("gastoPorDia");

                Parlamentar parlamentar = new Parlamentar(codigo, nome, sexo, cargo, urlFoto, partido, gastoTotal, gastoDia);
                spinnerParlamentaresArray.add(parlamentar.getNome());
                listaDados.add(parlamentar);
            }
            System.out.println("acabou a leitura");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void redirectToLoginRegisterView(){
        int timeout = 0; // make the activity visible for 4 seconds

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                finish();
                Intent homepage = new Intent(FilterActivity.this, LoginActivity.class);
                startActivity(homepage);
            }
        }, timeout);
    }

}
