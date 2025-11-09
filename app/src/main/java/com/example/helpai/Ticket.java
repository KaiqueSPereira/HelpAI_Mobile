// Em java/com/example/helpai/Ticket.java
package com.example.helpai;

import org.json.JSONObject;
import java.io.Serializable; // <-- IMPORT NECESSÁRIO

// Implementa Serializable para que o objeto possa ser passado em Intents
public class Ticket implements Serializable {

    private int idChamado;
    private String titulo;
    private String descricao;
    private String status;
    private String prioridade;
    private String categoria;
    private int idUsuario;
    private String userName;

    public Ticket(JSONObject json) {
        try {
            this.idChamado = json.getInt("id_chamado");
            this.titulo = json.getString("titulo");
            this.descricao = json.getString("descricao");
            this.status = json.getString("status_chamado");
            this.prioridade = json.getString("prioridade");
            this.categoria = json.getString("categoria");
            this.idUsuario = json.getInt("id_usuario");
            this.userName = json.optString("nome_usuario", "Usuário Desconhecido");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters
    public int getIdChamado() { return idChamado; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
    public String getPrioridade() { return prioridade; }
    public String getCategoria() { return categoria; }
    public int getUserId() { return idUsuario; }
    public String getUserName() { return userName; }
}