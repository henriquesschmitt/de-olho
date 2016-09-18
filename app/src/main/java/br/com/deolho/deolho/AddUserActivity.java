package br.com.deolho.deolho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import br.com.deolho.ws.UserInsertWS;

public class AddUserActivity extends AppCompatActivity {

    private ProgressDialog pd;
    EditText email;
    EditText senha;
    Button btnSaveUserRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        email = (EditText) findViewById(R.id.email_add_user);
        senha = (EditText) findViewById(R.id.password_add_user);

        //teste
//        email.setText("henriquesschmitt@gmail.com");
//        senha.setText("1212asa");

        btnSaveUserRegister = (Button) findViewById(R.id.email_register_button);
        btnSaveUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( (!email.getText().toString().equals("") && email.getText() != null) && (!senha.getText().toString().equals("") && senha.getText() != null) )
                    inserirUsuario(email.getText().toString(), senha.getText().toString());
                else
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Informe o campo email e senha", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });

    }

    public void inserirUsuario(final String usuario, final String senha){

        final UserInsertWS userInsertWS = new UserInsertWS();

        pd = ProgressDialog.show(AddUserActivity.this, "Aguarde", "Inserindo usuário...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                /** FAZ A REQUISIÇÃO DOS DADOS DO PARLAMENTAR SELECIONADO **/
                final boolean b = userInsertWS.inserirUsuario(usuario, senha);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(b) {
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
                    }
                });
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
