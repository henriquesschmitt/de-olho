package br.com.deolho.deolho;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import br.com.deolho.modelo.Parlamentar;
import br.com.deolho.ws.DespesasParlamentaresWS;

public class DetalheDespesaActivity extends AppCompatActivity {

    public static ImageView foto;
    public static TextView nome;
    public static TextView cargo;
    public static TextView data;
    public static TextView descricao;
    public static TextView beneficiado;
    public static TextView documento;
    public static TextView valor;
    public static RatingBar mBar;
    public Button btnEnviarAvaliacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_despesa);

        foto = (ImageView) findViewById(R.id.fotoParlamentar);
        nome = (TextView) findViewById(R.id.txtParlamentar);
        cargo = (TextView) findViewById(R.id.txtCargo);
        data = (TextView) findViewById(R.id.txtData);
        descricao = (TextView) findViewById(R.id.txtDescricao);
        beneficiado = (TextView) findViewById(R.id.txtBeneficiado);
        documento = (TextView) findViewById(R.id.txtDocumento);
        valor = (TextView) findViewById(R.id.txtValor);
        mBar = (RatingBar) findViewById(R.id.ratingBar);
        btnEnviarAvaliacao = (Button) findViewById(R.id.buttonAvaliation);

        nome.setText("Nome: " + MainActivity.despesaSelecionada.getNome());
        String cargoConv = MainActivity.despesaSelecionada.getTipoParlamentar().equals("s") ? "Senador(a)" : "Deputado(a)";
        cargo.setText("Cargo: " + cargoConv);
        data.setText("Data: " + MainActivity.despesaSelecionada.getData());
        descricao.setText("Descrição: " + MainActivity.despesaSelecionada.getDescricaoDespesa());
        beneficiado.setText("Beneficiado: " + MainActivity.despesaSelecionada.getFornecedor());
        documento.setText("Documento: " + MainActivity.despesaSelecionada.getDocumento());
        valor.setText("Valor: R$" + String.valueOf(MainActivity.despesaSelecionada.getValor()));

        MainActivity.pd = ProgressDialog.show(DetalheDespesaActivity.this, "Aguarde", "Carregando dados da despesa...");
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String nomeParlamentar = MainActivity.despesaSelecionada.getNome();
                    String[] nomesParseados = nomeParlamentar.split(" ");
                    final StringBuilder nomeParaConsulta = new StringBuilder();
                    for (int i = 0; i < nomesParseados.length; i++) {
                        nomeParaConsulta.append(nomesParseados[i]).append("%20");
                    }

                    DespesasParlamentaresWS despesasParlamentaresWS = new DespesasParlamentaresWS();
                    despesasParlamentaresWS.getFoto("http://" + MainActivity.IP_SERVIDOR + "/DespesasParlamentaresWS/resources/parlamentar/byname?nome=" + nomeParaConsulta.toString());

                    if (DespesasParlamentaresWS.fotoParlamentar != null && !DespesasParlamentaresWS.fotoParlamentar.equals(""))
                        new DownloadImageTask((ImageView) findViewById(R.id.fotoParlamentar))
                                .execute(DespesasParlamentaresWS.fotoParlamentar);

                    /** BUSCA A INFORMAÇÃO DA NOTA QUE ESSE USUARIO JÁ DEU (SE DEU) PRA ESSA DESEPSA VIA GET REQUEST **/
                    buscarNotaSeJaAvaliada(LoginActivity.ID_USUARIO_LOGADO, MainActivity.despesaSelecionada.getIdDespesa());

                }
            }).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        btnEnviarAvaliacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarAvaliacaoDespesa();
            }
        });

    }

    public void enviarAvaliacaoDespesa(){
        float notaAvaliada = mBar.getRating();

        /** TRAZ DADOS DE PARLAMENTARES **/
        MainActivity.pd = ProgressDialog.show(DetalheDespesaActivity.this, "Aguarde", "avaliando despesa...");
        persistirAvaliacaoDespesa(LoginActivity.ID_USUARIO_LOGADO, MainActivity.despesaSelecionada.getIdDespesa(), notaAvaliada);
    }

    public void setarBarraDeAvaliacao(Long nota){
        mBar.setRating(nota);
    }

    public void persistirAvaliacaoDespesa(final Long idUsuario, final Long idDespesa, final float nota){

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpGet = new HttpPost("http://"+ MainActivity.IP_SERVIDOR+"/DespesasParlamentaresWS/resources/avaliacao/add?idUsuario="+idUsuario+"&idDespesa="+idDespesa+"&nota="+nota+"");
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

                            if(stringBuilder1.toString().equals("[]")) {
                                MainActivity.pd.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Erro na avaliação da despesa :(", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                MainActivity.pd.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Despesa avaliada com sucesso :)", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            inputStream.close();

                        } else {
                            Log.d("JSON", "Failed to download file");
                        }

                    } catch (Exception e) {
                        System.out.println("erro = " + e.getMessage());
                    }
                }
            }).start();

        } catch (Exception e) {
            System.out.println("TRETA NA THREAD");
        }
    }

    public void buscarNotaSeJaAvaliada(final Long idUsuario, final Long idDespesa){

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://"+ MainActivity.IP_SERVIDOR+"/DespesasParlamentaresWS/resources/avaliacao/verify?idUsuario="+idUsuario+"&idDespesa="+idDespesa+"");
                    try {
                        HttpResponse response = httpClient.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        String line = null;
                        final StringBuilder stringBuilder1 = new StringBuilder();

                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream inputStream = entity.getContent();
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(inputStream));

                            while ((line = reader.readLine()) != null) {
                                stringBuilder1.append(line);
                            }

                            if(stringBuilder1.toString().equals("[]")) {
                                MainActivity.pd.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Você ainda não avaliou esta despesa", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                MainActivity.pd.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        setarBarraDeAvaliacao(new Long(stringBuilder1.toString()));
                                    }
                                });
                            }

                            inputStream.close();

                        } else {
                            Log.d("JSON", "Failed to download file");
                        }

                    } catch (Exception e) {
                        System.out.println("erro = " + e.getMessage());
                    }
                }
            }).start();

        } catch (Exception e) {
            System.out.println("TRETA NA THREAD");
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            String urlStr = params[0];
            Bitmap img = null;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlStr);
            HttpResponse response;
            try {
                response = (HttpResponse)client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                img = BitmapFactory.decodeStream(inputStream);

                MainActivity.pd.dismiss();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return img;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
