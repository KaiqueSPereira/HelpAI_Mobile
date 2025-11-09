// Em java/com/example/helpai/AnalyticsActivity.java
package com.example.helpai;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class AnalyticsActivity extends AppCompatActivity {

    private LineChart lineChart;
    private PieChart pieChart;
    private ProgressBar satisfactionProgressBar;
    private TextView tvSatisfactionPercentage, tvSatisfactionStatus;
    private BottomNavigationView bottomNavigation;

    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Recebe o tipo de usuário (para a navegação)
        userType = getIntent().getIntExtra(MainActivity.USER_TYPE_KEY, MainActivity.USER_TYPE_USER);

        // Encontra os Views
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        satisfactionProgressBar = findViewById(R.id.satisfactionProgressBar);
        tvSatisfactionPercentage = findViewById(R.id.tvSatisfactionPercentage);
        tvSatisfactionStatus = findViewById(R.id.tvSatisfactionStatus);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Configura os componentes
        setupBottomNavigation();
        setupLineChart();
        setupPieChart();
        setupSatisfactionGauge();
    }

    private void setupLineChart() {
        // 1. Dados de exemplo (Chamados por dia)
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 10));
        entries.add(new Entry(2, 5));
        entries.add(new Entry(3, 8));
        entries.add(new Entry(4, 12));
        entries.add(new Entry(5, 15));
        entries.add(new Entry(6, 10));

        LineDataSet dataSet = new LineDataSet(entries, "Chamados por Dia");

        // 2. Estilo (roxo, como no PIM)
        int purpleColor = ContextCompat.getColor(this, R.color.accentPurple);
        dataSet.setColor(purpleColor);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setCircleColor(purpleColor);
        dataSet.setCircleHoleColor(purpleColor);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10f);

        // 3. Configurações do Gráfico
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate(); // Desenha o gráfico
    }

    private void setupPieChart() {
        // 1. Dados de exemplo (baseado no PIM, Seção 5.6) 
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(3, "Críticos"));
        entries.add(new PieEntry(5, "Médio"));
        entries.add(new PieEntry(10, "Leves"));

        PieDataSet dataSet = new PieDataSet(entries, "Prioridade dos Chamados");

        // 2. Estilo (Cores e formato Donut)
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.priorityRed));
        colors.add(ContextCompat.getColor(this, R.color.priorityYellow));
        colors.add(ContextCompat.getColor(this, R.color.priorityGreen));
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(12f);

        // 3. Configurações do Gráfico (para parecer um Donut)
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false); // Desliga a legenda de cores
        pieChart.setDrawHoleEnabled(true); // <-- Isso faz ele virar um Donut
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(60f); // Tamanho do buraco
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setEntryLabelColor(Color.WHITE); // Cor dos textos (Críticos, Médio, Leves)
        pieChart.invalidate(); // Desenha o gráfico
    }

    private void setupSatisfactionGauge() {
        // Simula o mesmo dado da MainActivity (21%)
        int progress = 21;
        satisfactionProgressBar.setProgress(progress);
        tvSatisfactionPercentage.setText(progress + "%");
        tvSatisfactionStatus.setText("Good");
        // (Você pode adicionar a lógica de mudar a cor do "Good" se quiser)
    }

    private void setupBottomNavigation() {
        // O usuário que vê Analytics é Admin/Tec, então inflamos o menu ADM
        bottomNavigation.getMenu().clear();
        bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_adm);

        // Define o ícone de "Analytics" como selecionado
        bottomNavigation.setSelectedItemId(R.id.nav_analytics);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                intent = new Intent(AnalyticsActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                intent = new Intent(AnalyticsActivity.this, NewTicketActivity.class);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_edit) {
                intent = new Intent(AnalyticsActivity.this, TicketsActivity.class);
                intent.putExtra(MainActivity.FILTER_TYPE_KEY, MainActivity.FILTER_ALL);
                intent.putExtra(MainActivity.USER_TYPE_KEY, userType);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                Toast.makeText(this, "Tela de Listas (a ser criada)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                // Já estamos aqui
                return true;
            }
            return false;
        });
    }
}