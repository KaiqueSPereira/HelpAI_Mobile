package com.example.helpai;

import org.json.JSONObject;
import java.io.Serializable;

// Esta classe representa o usuário logado
public class Usuario implements Serializable {

    private int idUsuario;
    private String nome;
    private String email;
    private String setor;
    private int userType; // 0=USER, 1=TEC, 2=ADMIN

    // Construtor para popular a partir do JSON da API
    public Usuario(JSONObject json) {
        try {
            // Tenta ler os dados do JSON que a API enviou
            this.idUsuario = json.getInt("id_usuario");
            this.nome = json.getString("nome");
            this.email = json.getString("email");
            this.setor = json.optString("setor", "N/A"); // optString é seguro

            // O Backend deve nos dizer o perfil (baseado nas tabelas Usuario, Tecnico, Gerente)
            String perfil = json.optString("perfil", "USUARIO_COMUM");

            // Converte o texto do perfil para o int que o app usa
            switch (perfil) {
                case "ADMINISTRADOR":
                    this.userType = MainActivity.USER_TYPE_ADMIN;
                    break;
                case "TECNICO":
                    this.userType = MainActivity.USER_TYPE_TEC;
                    break;
                default:
                    this.userType = MainActivity.USER_TYPE_USER;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters (métodos para ler os dados)
    public int getIdUsuario() { return idUsuario; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSetor() { return setor; }
    public int getUserType() { return userType; }
}