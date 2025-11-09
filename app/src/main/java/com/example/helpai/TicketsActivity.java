package com.example.helpai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TicketsActivity extends AppCompatActivity {

    // Views
    private RecyclerView ticketsRecyclerView;
    private TicketAdapter ticketAdapter;
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar topAppBar;
    private TextView avatar;

    // Dados
    private List<Ticket> allTicketsList = new ArrayList<>();
    private List<Ticket> filteredTicketsList = new ArrayList<>();

    private int userType;
    private String filterType;
    private Usuario usuarioLogado; // <-- NOVO: Armazena o objeto para acesso ao ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        // 1. Receber os dados e o USUARIO_OBJ
        userType = getIntent().getIntExtra(MainActivity.USER_TYPE_KEY, MainActivity.USER_TYPE_USER);
        filterType = getIntent().getStringExtra(MainActivity.FILTER_TYPE_KEY);
        if (filterType == null) {
            filterType = MainActivity.FILTER_ALL;
        }

        // !! CORREÇÃO CRÍTICA AQUI !!
        // Recebe o objeto usuário do Intent e armazena na variável global
        if (getIntent().hasExtra("USUARIO_OBJ")) {
            usuarioLogado = (Usuario) getIntent().getSerializableExtra("USUARIO_OBJ");
        }

        // 2. Encontrar os Views
        topAppBar = findViewById(R.id.topAppBar);
        avatar = topAppBar.findViewById(R.id.avatar);
        ticketsRecyclerView = findViewById(R.id.tickets_recycler_view);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 3. Configurar a UI
        setupUI();
        setupBottomNavigation();
        setupRecyclerView();

        // 4. Carregar tickets
        loadTicketsFromApi();
    }

    // --- LÓGICA DE UI E SETUP ---
    private void setupUI() {
        if (usuarioLogado != null) {
            avatar.setText(String.valueOf(usuarioLogado.getNome().charAt(0)));
        } else {
            avatar.setText("U");
        }
    }

    private void setupRecyclerView() {
        // Passa o userType para o adaptador (para a próxima tela de detalhe)
        ticketAdapter = new TicketAdapter(this, filteredTicketsList, userType);
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ticketsRecyclerView.setAdapter(ticketAdapter);
    }

    // --- FILTRAGEM E CARREGAMENTO DE DADOS ---

    private void loadTicketsFromApi() {
        // (Idealmente, mostre um ProgressBar aqui)
        ApiClient.getInstance().getChamados(new ApiClient.ApiCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray chamadosArray) {
                allTicketsList.clear();
                try {
                    // Converte o Array de JSON em uma Lista de Objetos Ticket
                    for (int i = 0; i < chamadosArray.length(); i++) {
                        JSONObject ticketJson = chamadosArray.getJSONObject(i);
                        allTicketsList.add(new Ticket(ticketJson));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Agora que temos os dados, filtramos e atualizamos a lista
                filterData();
                ticketAdapter.notifyDataSetChanged();
                // (Esconda o ProgressBar aqui)
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TicketsActivity.this, "Erro ao carregar chamados: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * (CORRIGIDO) Filtra a lista usando loops simples (compatível com API 21).
     */
    private void filterData() {
        filteredTicketsList.clear();

        List<Ticket> tempStream = new ArrayList<>();

        // Pega o ID do usuário logado (usando a variável global)
        int loggedInUserId = (usuarioLogado != null) ? usuarioLogado.getIdUsuario() : -1;

        // 1. Aplica o filtro de Usuário
        for (Ticket ticket : allTicketsList) {
            if (userType == MainActivity.USER_TYPE_USER) {
                // APENAS ADICIONA SE O ID BATER
                if (ticket.getUserId() == loggedInUserId) {
                    tempStream.add(ticket);
                }
            }
            else {
                // Admin/Tec veem todos
                tempStream.add(ticket);
            }
        }

        // 2. Aplica o filtro de Status (Prioridade, Resolvido, ou Todos)
        List<Ticket> finalFilteredList = new ArrayList<>();
        for (Ticket ticket : tempStream) {
            if (MainActivity.FILTER_PRIORITY.equals(filterType)) {
                if (ticket.getPrioridade().equals("Alta")) {
                    finalFilteredList.add(ticket);
                }
            }
            else if (MainActivity.FILTER_RESOLVED.equals(filterType)) {
                if (ticket.getStatus().equals("Resolvido")) {
                    finalFilteredList.add(ticket);
                }
            }
            else {
                // FILTER_ALL e outros (o que sobrou)
                finalFilteredList.add(ticket);
            }
        }

        filteredTicketsList.addAll(finalFilteredList);
    }

    // --- NAVEGAÇÃO ---

    private void setupBottomNavigation() {
        if (userType == MainActivity.USER_TYPE_TEC || userType == MainActivity.USER_TYPE_ADMIN) {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_adm);
        } else {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_user);
        }

        if (MainActivity.FILTER_RESOLVED.equals(filterType)) {
            bottomNavigation.setSelectedItemId(R.id.nav_list);
        } else {
            bottomNavigation.setSelectedItemId(R.id.nav_edit);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;

            // Passa o objeto USUARIO_OBJ para todas as telas
            Serializable userObj = getIntent().getSerializableExtra("USUARIO_OBJ");

            if (itemId == R.id.nav_home) {
                intent = new Intent(TicketsActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", userObj);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                intent = new Intent(TicketsActivity.this, NewTicketActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", userObj);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_edit) {
                intent = new Intent(TicketsActivity.this, TicketsActivity.class);
                intent.putExtra(MainActivity.FILTER_TYPE_KEY, MainActivity.FILTER_ALL);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", userObj);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                intent = new Intent(TicketsActivity.this, TicketsActivity.class);
                intent.putExtra(MainActivity.FILTER_TYPE_KEY, MainActivity.FILTER_RESOLVED);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", userObj);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                intent = new Intent(TicketsActivity.this, AnalyticsActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", userObj);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}