package com.example.helpai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etSenha;
    private MaterialButton btnGo;
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Encontrar Views
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnGo = findViewById(R.id.btnGo);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        // Configurar clique do botão
        btnGo.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String senha = etSenha.getText().toString();

            // Validação simples
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha o e-mail e a senha", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading(true);

            // !! LÓGICA DE API REAL !!
            // Chama o ApiClient para fazer o login
            ApiClient.getInstance().login(email, senha, new ApiClient.ApiCallback<Usuario>() {

                @Override
                public void onSuccess(Usuario usuario) {
                    // Sucesso! A API retornou um usuário
                    showLoading(false);

                    // Inicia a MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // Envia o tipo de usuário REAL vindo da API
                    intent.putExtra(MainActivity.USER_TYPE_KEY, usuario.getUserType());
                    // (Opcional: enviar o objeto Usuario inteiro para o Profile)
                    intent.putExtra("USUARIO_OBJ", usuario);
                    startActivity(intent);
                    finish(); // Fecha a tela de Login
                }

                @Override
                public void onError(String error) {
                    // Erro! (Ex: senha errada, rede, etc)
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, "Erro de Login: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loginProgressBar.setVisibility(View.VISIBLE);
            btnGo.setEnabled(false);
            btnGo.setText("Entrando...");
        } else {
            loginProgressBar.setVisibility(View.GONE);
            btnGo.setEnabled(true);
            btnGo.setText("GO");
        }
    }
}