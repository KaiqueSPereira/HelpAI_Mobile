package com.example.helpai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class NewTicketActivity extends AppCompatActivity {

    // --- Views ---
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar topAppBar;
    private TextView avatar;
    private Spinner spinnerCategoria;
    private EditText etTitulo, etDescricao;
    private Button btnAbrirChamado;
    private ProgressBar loadingProgressBar;

    // --- Variáveis ---
    private int userType = MainActivity.USER_TYPE_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);

        userType = getIntent().getIntExtra(MainActivity.USER_TYPE_KEY, MainActivity.USER_TYPE_USER);

        // --- Encontrar Views ---
        topAppBar = findViewById(R.id.topAppBar);
        avatar = topAppBar.findViewById(R.id.avatar);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        etTitulo = findViewById(R.id.etTitulo);
        etDescricao = findViewById(R.id.etDescricao);
        btnAbrirChamado = findViewById(R.id.btnAbrirChamado);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        loadingProgressBar = findViewById(R.id.progressBar);

        // --- Configurações ---
        setupSpinner();
        setupBottomNavigation();

        // (Configura o avatar)
        if (getIntent().hasExtra("USUARIO_OBJ")) {
            Usuario usuario = (Usuario) getIntent().getSerializableExtra("USUARIO_OBJ");
            if (usuario != null) {
                avatar.setText(String.valueOf(usuario.getNome().charAt(0)));
            }
        }

        // --- Configurar Cliques ---
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(NewTicketActivity.this, ProfileActivity.class);
            intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
            intent.putExtra("USUARIO_OBJ", getIntent().getSerializableExtra("USUARIO_OBJ"));
            startActivity(intent);
        });

        btnAbrirChamado.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString();
            String categoria = spinnerCategoria.getSelectedItem().toString();
            String descricao = etDescricao.getText().toString();

            if (titulo.isEmpty() || categoria.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(NewTicketActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading(true);

            // !! CHAMA A NOSSA API !!
            // O Backend deve chamar a IA e salvar no banco.
            ApiClient.getInstance().createChamado(titulo, categoria, descricao, new ApiClient.ApiCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    showLoading(false);
                    Toast.makeText(NewTicketActivity.this, result, Toast.LENGTH_LONG).show();

                    // Volta para a MainActivity
                    Intent intent = new Intent(NewTicketActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                    intent.putExtra("USUARIO_OBJ", getIntent().getSerializableExtra("USUARIO_OBJ"));
                    // Limpa as telas anteriores para não "voltar" para o formulário
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(NewTicketActivity.this, "Erro: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            btnAbrirChamado.setEnabled(false);
            btnAbrirChamado.setText("Analisando...");
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            btnAbrirChamado.setEnabled(true);
            btnAbrirChamado.setText("Abrir Novo Chamado");
        }
    }

    // --- (Seus outros métodos setupSpinner e setupBottomNavigation
    //      (com todas as 5 opções de navegação) continuam aqui) ---
    // (Cole-os aqui)
    private void setupSpinner() {
        String[] categorias = getResources().getStringArray(R.array.categoria_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(NewTicketActivity.this,
                android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        if (userType == MainActivity.USER_TYPE_TEC || userType == MainActivity.USER_TYPE_ADMIN) {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_adm);
        } else {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_user);
        }
        bottomNavigation.setSelectedItemId(R.id.nav_add);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                intent = new Intent(NewTicketActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", getIntent().getSerializableExtra("USUARIO_OBJ"));
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                return true;
            } else if (itemId == R.id.nav_edit) {
                intent = new Intent(NewTicketActivity.this, TicketsActivity.class);
                intent.putExtra(MainActivity.FILTER_TYPE_KEY, MainActivity.FILTER_ALL);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", getIntent().getSerializableExtra("USUARIO_OBJ"));
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                intent = new Intent(NewTicketActivity.this, TicketsActivity.class);
                intent.putExtra(MainActivity.FILTER_TYPE_KEY, MainActivity.FILTER_RESOLVED);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", getIntent().getSerializableExtra("USUARIO_OBJ"));
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                intent = new Intent(NewTicketActivity.this, AnalyticsActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", getIntent().getSerializableExtra("USUARIO_OBJ"));
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}