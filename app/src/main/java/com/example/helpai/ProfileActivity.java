package com.example.helpai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    // Views da tela
    private TextView tvProfileName, tvProfileAccountType, tvProfileSector;
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar topAppBar;
    private MaterialButton btnLogout;


    private int userType = MainActivity.USER_TYPE_USER; // Padrão

    // Dados simulados
    private String userName = "Kaique Silva";
    // O tipo de conta agora é dinâmico
    private String sector = "Tecnologia da Informação";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // (NOVO) Lê o tipo de usuário que veio da tela anterior
        userType = getIntent().getIntExtra(MainActivity.USER_TYPE_KEY, MainActivity.USER_TYPE_USER);

        // --- Encontrar Views ---
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileAccountType = findViewById(R.id.tvProfileAccountType);
        tvProfileSector = findViewById(R.id.tvProfileSector);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        topAppBar = findViewById(R.id.topAppBar);
        btnLogout = findViewById(R.id.btnLogout);

        // --- Carregar Dados do Perfil ---
        loadProfileData();

        // --- Configurar Navegação Inferior ---
        setupBottomNavigation();
    }

    /**
     * (ATUALIZADO) Carrega os dados e define o tipo de conta
     */
    private void loadProfileData() {
        String accountTypeString;

        // (NOVO) Converte o 'int' do tipo de usuário em um texto
        switch (userType) {
            case MainActivity.USER_TYPE_TEC:
                accountTypeString = "Técnico";
                break;
            case MainActivity.USER_TYPE_ADMIN:
                accountTypeString = "Administrador";
                break;
            case MainActivity.USER_TYPE_USER:
            default:
                accountTypeString = "Usuário Comum";
                break;
        }

        tvProfileName.setText("Nome: " + userName);
        tvProfileAccountType.setText("Conta: " + accountTypeString); // ⬅️ Mostra o tipo
        tvProfileSector.setText("Setor: " + sector);
    }

    /**
     * (ATUALIZADO) Configura a navegação para ADM, TEC ou Usuário
     */
    private void setupBottomNavigation() {
        // 1. Infla o menu correto
        if (userType == MainActivity.USER_TYPE_TEC || userType == MainActivity.USER_TYPE_ADMIN) {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_adm);
        } else {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_user);
        }

        // 2. Desmarca todos os itens (nenhum está selecionado)
        bottomNavigation.setSelectedItemId(0); // Nenhum item selecionado

        // 3. Adiciona o listener
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;

            if (itemId == R.id.nav_home) {
                intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                intent = new Intent(ProfileActivity.this, NewTicketActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_edit) {
                intent = new Intent(ProfileActivity.this, TicketsActivity.class);
                intent.putExtra(MainActivity.FILTER_TYPE_KEY, MainActivity.FILTER_ALL);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                Toast.makeText(this, "Tela de Listas (a ser criada)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                intent = new Intent(ProfileActivity.this, AnalyticsActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(intent);
                finish();


                return true;
            }

            return false;
        });
    }
}