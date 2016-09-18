package br.com.deolho.deolho;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

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
            Thread t = new Thread(new Runnable() {
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
                }
            });
            t.start();
            t.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (DespesasParlamentaresWS.fotoParlamentar != null && !DespesasParlamentaresWS.fotoParlamentar.equals(""))
            new DownloadImageTask((ImageView) findViewById(R.id.fotoParlamentar))
                    .execute(DespesasParlamentaresWS.fotoParlamentar);

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
