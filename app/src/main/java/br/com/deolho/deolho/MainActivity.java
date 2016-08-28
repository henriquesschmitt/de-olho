package br.com.deolho.deolho;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.Charset;

import br.com.deolho.listagem.CustomList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView list;
    String[] web = {
            "Fornecedor: H PLUS ADMINISTRAÇÃO E HOTELARIA LTDA - SCP ATHOS BULCÃO sda sad sad sad ad asd asdas das",
            "Fornecedor: TRANSCONTINENTAL AGÊNCIA DE VIAGENS LTDA",
            "Fornecedor: HRPP SERVIÇO DE PRODUÇÃO E IMP. DE MATERIAIS LTDA-ME",
            "Fornecedor: TRANSCONTINENTAL AGÊNCIA DE VIAGENS LTDA",
            "Fornecedor: TRANSCONTINENTAL AGÊNCIA DE VIAGENS LTDA",
            "Fornecedor: TRANSCONTINENTAL AGÊNCIA DE VIAGENS LTDA",
            "Fornecedor: TRANSCONTINENTAL AGÊNCIA DE VIAGENS LTDA"
    };
    Integer[] imageId = {
            R.drawable.ladrao1,
            R.drawable.ladrao1,
            R.drawable.ladrao1,
            R.drawable.ladrao1,
            R.drawable.anaamelialemos,
            R.drawable.ladrao1,
            R.drawable.anaamelialemos
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //CONFIGURAÇÃO DO LIST VIEW
        CustomList adapter = new
                CustomList(MainActivity.this, web, imageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        //AÇÃO DO CLIQUE NO LIST VIEW
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
            }
        });

        getDespesas();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void getDespesas(){
        try {
//            String sURL = "http://localhost:8080/DespesasParlamentaresWS/resources/email";
//
//            URL url = new URL(sURL);
//            HttpURLConnection request = (HttpURLConnection) url.openConnection();
//            request.connect();
//
//            // Convert to a JSON object to print data
//            JsonParser jp = new JsonParser(); //from gson
//            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
//            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
//            zipcode = rootobj.get("zip_code").getAsString(); //just grab the zipcode

            JSONObject json = new JSONObject(IOUtils.toString(new URL("https://graph.facebook.com/me"), Charset.forName("UTF-8")));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
