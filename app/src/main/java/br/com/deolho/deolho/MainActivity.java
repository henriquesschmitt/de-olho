package br.com.deolho.deolho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import br.com.deolho.modelo.Despesa;
import br.com.deolho.modelo.Parlamentar;
import br.com.deolho.util.CustomList;
import br.com.deolho.ws.DespesasParlamentaresWS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ProgressDialog pd;
    public static List<Parlamentar> listaDadosParlamentares = null;
    Spinner sParlamentares;
    List<String> spinnerParlamentaresArray =  new ArrayList<String>();
    List<Despesa> despesaList = new ArrayList<>();

    ListView list;

    public String[] listViewDescription = new String[1000];
    public int[] listViewImage = new int[1000];
    FloatingActionButton fab;

    public static Despesa despesaSelecionada = null;

    public static String IP_SERVIDOR = "10.0.0.101:8080";

    public static BigDecimal valTotalDespesas = new BigDecimal(BigDecimal.ROUND_UP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /** TRAZ DADOS DE PARLAMENTARES **/
        pd = ProgressDialog.show(MainActivity.this, "Aguarde", "Atualizando dados de parlamentares");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getDadosParlamentares("http://meucongressonacional.com/api/001/senador", 0);
                        }
                    });
                    t1.start();
                    t1.join();

                    //start a new thread to process job
                    Thread t2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getDadosParlamentares("http://meucongressonacional.com/api/001/deputado", 1);
                        }
                    });
                    t2.start();
                    t2.join();

//            /**
//             * Ordenar o array list
//             * **/
//            Collections.sort (listaDadosParlamentares, new Comparator() {
//                public int compare(Object o1, Object o2) {
//                    Parlamentar c1 = (Parlamentar) o1;
//                    Parlamentar c2 = (Parlamentar) o2;
//                    return c1.getNome().compareToIgnoreCase(c2.getNome());
//                }
//            });

                } catch (Exception e) {}
            }
        }).start();

        spinnerParlamentaresArray.add("Selecione um parlamentar");
        ArrayAdapter<String> adapterParlamentar = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerParlamentaresArray);
        adapterParlamentar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sParlamentares = (Spinner) findViewById(R.id.spinnerParlamentar);
        sParlamentares.setAdapter(adapterParlamentar);

        final DespesasParlamentaresWS despesasParlamentaresWS = new DespesasParlamentaresWS();

        /** AÇÃO DE SELEÇÃO DE PARLAMENTAR NO SPINNER **/
        sParlamentares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(position != 0) {

                    String nomeParlamentar = sParlamentares.getSelectedItem().toString();
                    String[] nomesParseados = nomeParlamentar.split(" ");
                    final StringBuilder nomeParaConsulta = new StringBuilder();
                    for	(int i=0; i<nomesParseados.length;i++){
                        nomeParaConsulta.append(nomesParseados[i]).append("%20");
                    }

                    pd = ProgressDialog.show(MainActivity.this, "Aguarde", "Pesquisando despesas do parlamentar...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            /** FAZ A REQUISIÇÃO DOS DADOS DO PARLAMENTAR SELECIONADO **/
                            despesaList = despesasParlamentaresWS.getDespesaParlamentares("http://"+IP_SERVIDOR+"/DespesasParlamentaresWS/resources/despesa/byname?nome=" + nomeParaConsulta.toString());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String[] descricaoDespesa = new String[despesaList.size()];
                                    String[] valorDespesa = new String[despesaList.size()];
                                    int[] posicaoImagemParlamentar = new int[despesaList.size()];
                                    valTotalDespesas = new BigDecimal(String.valueOf(BigDecimal.ZERO));
                                    for (int i=0;i<despesaList.size();i++){
                                        descricaoDespesa[i] = despesaList.get(i).getDescricaoDespesa();

                                        //SOMA DESPESAS
                                        valTotalDespesas = valTotalDespesas.add(despesaList.get(i).getValor());

                                        valorDespesa [i] = "Valor: R$" + String.valueOf(despesaList.get(i).getValor());
                                        posicaoImagemParlamentar[i] = 0;
                                    }

                                    configuracaoListView(descricaoDespesa, valorDespesa, posicaoImagemParlamentar);
                                }
                            });
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //CLIQUE NO FAB NO CANTO DA TELA
        fabActionListener();

        String[] descricaoDespesa = new String[despesaList.size()];
        String[] valorDespesa = new String[despesaList.size()];
        int[] posicaoImagemParlamentar = new int[despesaList.size()];
        for (int i=0;i<despesaList.size();i++){
            descricaoDespesa[i] = String.valueOf(i);
            valorDespesa [i] = "Valor: R$" + String.valueOf(despesaList.get(i).getValor());
            posicaoImagemParlamentar[i] = 0;
        }
        configuracaoListView(descricaoDespesa, valorDespesa, posicaoImagemParlamentar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void fabActionListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(despesaList.size() == 0){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Selecione um parlamentar", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(new Locale ("pt", "BR"));
                    usdCostFormat.setMinimumFractionDigits( 1 );
                    usdCostFormat.setMaximumFractionDigits( 2 );
                    Snackbar.make(view, "Valor total: " + String.valueOf(usdCostFormat.format(valTotalDespesas.doubleValue())), Snackbar.LENGTH_LONG)
                            .setAction("Total de despesas do Parlamentar", null).show();
                }
            }
        });
    }

    public void configuracaoListView(String[] abc, String[] valor, int[] def){
        CustomList adapter = new
                CustomList(MainActivity.this, abc, valor, def);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        //AÇÃO DO CLIQUE NO LIST VIEW
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            /** SETA VALORES QUE SERÃO EXIBIDOS NA TELA DE DETALHES DA DESPESA **/
            despesaSelecionada = despesaList.get(position);
            redirectToDetailBand();
            }
        });
        list.setEmptyView(findViewById(R.id.emptyElement));
    }

    public void redirectToDetailBand(){
        int timeout = 0;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                finish();
                Intent homepage = new Intent(MainActivity.this, DetalheDespesaActivity.class);
                startActivity(homepage);
            }
        }, timeout);
    }

    public void getDadosParlamentares(final String url, final int index){

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

                                List<Parlamentar> listaTemp = retornaListaDadosParlamentares(stringBuilder1.toString(), index);
                                for (Parlamentar parlamentar : listaTemp) {
                                    listaDadosParlamentares.add(parlamentar);
                                }

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
            t.start(); // spawn thread
            t.join();  // wait for thread to finish

        } catch (Exception e) {
            System.out.println("TRETA NA THREAD");
        }
    }

    public List<Parlamentar> retornaListaDadosParlamentares(String jsonLido, int index){

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
            if(index == 1)
                pd.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaDados;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
