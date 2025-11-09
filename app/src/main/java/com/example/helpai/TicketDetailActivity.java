// Em java/com/example/helpai/TicketDetailActivity.java
package com.example.helpai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TicketDetailActivity extends AppCompatActivity {

    // Views
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigation;
    private TextView tvTitle, tvStatus, tvPriority, tvDescription;

    // Dados
    private int userType;
    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        // 1. Receber os dados do "pacote" (Intent)
        Intent intent = getIntent();
        if (intent.hasExtra("TICKET_DATA") && intent.hasExtra(MainActivity.USER_TYPE_KEY)) {
            ticket = (Ticket) intent.getSerializableExtra("TICKET_DATA");
            userType = intent.getIntExtra(MainActivity.USER_TYPE_KEY, MainActivity.USER_TYPE_USER);
        } else {
            // Se algo der errado, mostra um erro e fecha a tela
            Toast.makeText(this, "Erro ao carregar dados do chamado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Encontrar os Views
        topAppBar = findViewById(R.id.topAppBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvPriority = findViewById(R.id.tvDetailPriority);
        tvDescription = findViewById(R.id.tvDetailDescription);

        // 3. Configurar a UI
        setupToolbar();
        setupBottomNavigation();
        loadTicketData();
    }

    // Configura o botão de "Voltar"
    private void setupToolbar() {
        topAppBar.setNavigationOnClickListener(v -> {
            finish(); // Fecha a tela atual e volta para a lista
        });
    }

    // Preenche os dados do ticket nos TextViews
    private void loadTicketData() {
        tvTitle.setText(ticket.getTitulo());
        tvStatus.setText(ticket.getStatus());
        tvPriority.setText(ticket.getPrioridade());
        tvDescription.setText(ticket.getDescricao());
    }

    // Configura a barra de navegação (igual às outras telas)
    private void setupBottomNavigation() {
        if (userType == MainActivity.USER_TYPE_USER) {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_user);
        } else {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_adm);
        }

        // Define o ícone de "Chamados" (nav_edit) como selecionado
        bottomNavigation.setSelectedItemId(R.id.nav_edit);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent navIntent;

            if (itemId == R.id.nav_home) {
                navIntent = new Intent(TicketDetailActivity.this, MainActivity.class);
                navIntent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(navIntent);
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                navIntent = new Intent(TicketDetailActivity.this, NewTicketActivity.class);
                navIntent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(navIntent);
                finish();
                return true;
            } else if (itemId == R.id.nav_edit) {
                // Já estamos na seção de "Chamados"
                // Apenas fecha esta tela e volta para a lista (TicketsActivity)
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                Toast.makeText(this, "Tela de Listas (a ser criada)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                Toast.makeText(this, "Tela de Analytics (a ser criada)", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}