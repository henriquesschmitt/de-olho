package br.com.deolho.deolho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import br.com.deolho.ws.UsuarioInsertWS;
import br.com.deolho.ws.UsuarioVerifyWS;

public class AddUserActivity extends AppCompatActivity {

    private ProgressDialog pd;
    EditText email;
    EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        email = (EditText) findViewById(R.id.email_add_user);
        senha = (EditText) findViewById(R.id.password_add_user);

        //teste
//        email.setText("henriquesschmitt@gmail.com");
//        senha.setText("1212asa");

        Button btnSaveUserRegister = (Button) findViewById(R.id.email_register_button);
        btnSaveUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirUsuario(email.getText().toString(), senha.getText().toString());
            }
        });

    }

    public void inserirUsuario(final String usuario, final String senha){

        pd = ProgressDialog.show(this, "Aguarde",
                "Registrando usuário...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                UsuarioInsertWS usuarioInsertWS = new UsuarioInsertWS();
                try {
                    Boolean a = usuarioInsertWS.execute(usuario, senha).get();
                    if(a) {
                        redirectToLoginRegisterView();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Usuário inserido com sucesso!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        pd.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Erro ao registrar usuário", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void redirectToLoginRegisterView(){
        int timeout = 0; // make the activity visible for 4 seconds

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                finish();
                Intent homepage = new Intent(AddUserActivity.this, LoginActivity.class);
                startActivity(homepage);
            }
        }, timeout);
    }

}
