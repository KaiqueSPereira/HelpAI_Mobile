package com.example.helpai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.WindowCompat;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // --- Constantes (não mudam) ---
    public static final String USER_TYPE_KEY = "USER_TYPE";
    public static final int USER_TYPE_USER = 0;
    public static final int USER_TYPE_TEC = 1;
    public static final int USER_TYPE_ADMIN = 2;
    public static final String FILTER_TYPE_KEY = "FILTER_TYPE";
    public static final String FILTER_ALL = "ALL";
    public static final String FILTER_PRIORITY = "PRIORITY";
    public static final String FILTER_RESOLVED = "RESOLVED";

    // --- Variáveis de Dados (não mais simuladas) ---
    private int userType;
    private Usuario usuarioLogado; // (Opcional, vindo do Login)

    // --- Views ---
    private BottomNavigationView bottomNavigation;
    private TextView tvGreeting;
    private MaterialToolbar topAppBar;
    private TextView avatar;
    private View cardOpenTasks, cardPriorityTasks, cardInProgress;
    private TextView tvOpenTasksTitle, tvOpenTasksCount;
    private TextView tvPriorityTasksTitle, tvPriorityTasksCount;
    private ProgressBar progressBar;
    private TextView tvProgressPercentage, tvProgressStatus, tvLastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // (A Splash Screen SÓ deve estar na LoginActivity)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recebe o tipo de usuário REAL da LoginActivity
        userType = getIntent().getIntExtra(MainActivity.USER_TYPE_KEY, MainActivity.USER_TYPE_USER);
        // (Opcional) Recebe o objeto usuário inteiro
        if (getIntent().hasExtra("USUARIO_OBJ")) {
            usuarioLogado = (Usuario) getIntent().getSerializableExtra("USUARIO_OBJ");
        }

        setupViews();
        setupListeners();
        setupUI(); // Configura a UI (ex: esconde card) com base no userType real

        // Carrega os dados reais da API
        loadUserData();
        loadDashboardData();
    }

    private void setupViews() {
        // (Seu código setupViews() existente está perfeito, não mude)
        topAppBar = findViewById(R.id.topAppBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        avatar = topAppBar.findViewById(R.id.avatar);
        tvGreeting = findViewById(R.id.tvGreeting);
        cardOpenTasks = findViewById(R.id.cardOpenTasks);
        tvOpenTasksTitle = cardOpenTasks.findViewById(R.id.tvCardTitle);
        tvOpenTasksCount = cardOpenTasks.findViewById(R.id.tvCardCount);
        cardPriorityTasks = findViewById(R.id.cardPriorityTasks);
        tvPriorityTasksTitle = cardPriorityTasks.findViewById(R.id.tvCardTitle);
        tvPriorityTasksCount = cardPriorityTasks.findViewById(R.id.tvCardCount);
        cardInProgress = findViewById(R.id.cardInProgress);
        progressBar = cardInProgress.findViewById(R.id.progressBar);
        tvProgressPercentage = cardInProgress.findViewById(R.id.tvProgressPercentage);
        tvProgressStatus = cardInProgress.findViewById(R.id.tvProgressStatus);
        tvLastUpdate = cardInProgress.findViewById(R.id.tvLastUpdate);
    }

    // --- MÉTODOS DE CARREGAMENTO DE DADOS (ATUALIZADOS) ---

    private void loadUserData() {
        if (usuarioLogado != null) {
            tvGreeting.setText("Ola " + usuarioLogado.getNome() + ", como podemos ajudar?");
            avatar.setText(String.valueOf(usuarioLogado.getNome().charAt(0)));
        } else {
            tvGreeting.setText("Ola, como podemos ajudar?");
            avatar.setText("U");
        }
    }

    /**
     * Busca os números do dashboard na API.
     */
    private void loadDashboardData() {
        ApiClient.getInstance().getDashboardStats(new ApiClient.ApiCallback<DashboardStats>() {
            @Override
            public void onSuccess(DashboardStats stats) {
                // Atualiza o card "Abertas"
                tvOpenTasksTitle.setText("Taskys abertas");
                tvOpenTasksCount.setText(String.valueOf(stats.getAbertas()));

                // Atualiza o card "Prioritárias" (se visível)
                if (userType != USER_TYPE_USER) {
                    tvPriorityTasksTitle.setText("Taskys Prioritárias"); // (Ou o texto que a API mandar)
                    tvPriorityTasksCount.setText(String.valueOf(stats.getPrioritarias()));
                }

                // Atualiza o medidor de progresso
                int progressoAtual = stats.getProgresso();
                progressBar.setProgress(progressoAtual);
                tvProgressPercentage.setText(progressoAtual + "%");
                updateTaskStatus(progressoAtual);
                tvLastUpdate.setText("Atualizado agora"); // (Idealmente viria da API)
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Erro ao carregar dashboard: " + error, Toast.LENGTH_SHORT).show();
                // Mostra "0" em caso de erro
                tvOpenTasksCount.setText("0");
                tvPriorityTasksCount.setText("0");
                progressBar.setProgress(0);
                tvProgressPercentage.setText("0%");
            }
        });
    }

    // --- (Seus outros métodos setupListeners, setupCardClicks, setupBottomNavigation,
    //      setupUI, e updateTaskStatus continuam exatamente iguais) ---
    // (Cole-os aqui)

    // (O restante do seu código MainActivity.java...)
    private void setupListeners() {
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra(USER_TYPE_KEY, userType);
            intent.putExtra("USUARIO_OBJ", usuarioLogado); // Passa o usuário para o Profile
            startActivity(intent);
        });
        setupCardClicks();
        setupBottomNavigation();
    }

    private void setupCardClicks() {
        cardOpenTasks.setOnClickListener(v -> openTicketsActivity(FILTER_ALL));
        if (userType == USER_TYPE_TEC || userType == USER_TYPE_ADMIN) {
            cardPriorityTasks.setOnClickListener(v -> openTicketsActivity(FILTER_PRIORITY));
        }
    }

    private void openTicketsActivity(String filter) {
        Intent intent = new Intent(MainActivity.this, TicketsActivity.class);
        intent.putExtra(FILTER_TYPE_KEY, filter);
        intent.putExtra(USER_TYPE_KEY, userType);
        intent.putExtra("USUARIO_OBJ", usuarioLogado);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_add) {
                intent = new Intent(MainActivity.this, NewTicketActivity.class);
                intent.putExtra(USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", usuarioLogado);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_edit) {
                openTicketsActivity(FILTER_ALL);
                return true;
            } else if (itemId == R.id.nav_list) {
                openTicketsActivity(FILTER_RESOLVED); // Chama o filtro de resolvidos
                return true;
            } else if (itemId == R.id.nav_analytics) {
                intent = new Intent(MainActivity.this, AnalyticsActivity.class);
                intent.putExtra(USER_TYPE_KEY, userType);
                intent.putExtra("USUARIO_OBJ", usuarioLogado);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupUI() {
        if (userType == USER_TYPE_USER) {
            cardPriorityTasks.setVisibility(View.GONE);
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_user);
        } else {
            cardPriorityTasks.setVisibility(View.VISIBLE);
            cardPriorityTasks.setBackgroundResource(R.drawable.priority_card_background);
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_adm);
        }
    }

    private void updateTaskStatus(int progressPercentage) {
        String statusText;
        int statusColorResId;
        if (progressPercentage < 30) { statusText = "Bad"; statusColorResId = R.color.statusBadRed; }
        else if (progressPercentage < 70) { statusText = "Warning"; statusColorResId = R.color.statusWarningYellow; }
        else { statusText = "Good"; statusColorResId = R.color.statusGoodPurple; }
        tvProgressStatus.setText(statusText);
        int statusColor = ContextCompat.getColor(this, statusColorResId);
        Drawable backgroundDrawable = tvProgressStatus.getBackground();
        Drawable tintedDrawable = DrawableCompat.wrap(backgroundDrawable).mutate();
        DrawableCompat.setTint(tintedDrawable, statusColor);
        tvProgressStatus.setBackground(tintedDrawable);
    }
}