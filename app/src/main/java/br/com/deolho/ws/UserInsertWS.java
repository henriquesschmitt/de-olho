package br.com.deolho.ws;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.deolho.deolho.MainActivity;

/**
 * Created by dell on 17/09/2016.
 */
public class UserInsertWS {

    public boolean inserirUsuario(String email, String senha){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://"+ MainActivity.IP_SERVIDOR+"/DespesasParlamentaresWS/resources/usuario/insert?email=" + email +
                "&senha=" + senha);
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

                inputStream.close();

            } else {
                Log.d("JSON", "Failed to download file");
            }

            if(!stringBuilder1.toString().equals("[]"))
                return true;

        } catch (Exception e) {
            System.out.println("erro = " + e.getMessage());
        }
        return false;
    }
}
