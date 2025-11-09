package com.example.helpai;

import org.json.JSONObject;

// Classe simples para guardar os números do Dashboard
public class DashboardStats {
    private int abertas;
    private int prioritarias;
    private int progresso;

    public DashboardStats(JSONObject json) {
        try {
            this.abertas = json.getInt("abertas");
            this.prioritarias = json.getInt("prioritarias");
            this.progresso = json.getInt("progresso");
        } catch (Exception e) {
            e.printStackTrace();
            // Define valores padrão em caso de erro no JSON
            this.abertas = 0;
            this.prioritarias = 0;
            this.progresso = 0;
        }
    }

    // Getters
    public int getAbertas() { return abertas; }
    public int getPrioritarias() { return prioritarias; }
    public int getProgresso() { return progresso; }
}