package com.example.helpai;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe central (Singleton) para gerenciar todas as chamadas de API do HelpAI.
 * Usa a biblioteca nativa HttpURLConnection.
 */
public class ApiClient {

    // !! IMPORTANTE !!
    // Este é o endereço do "localhost" do seu computador (PC/Mac)
    // visto de dentro do Emulador Android.
    // Quando sua API estiver rodando no 'localhost:8080', o emulador
    // a encontrará usando '10.0.2.2:8080'.
    private static final String API_BASE_URL = "https://cleveland-untastable-dusty.ngrok-free.dev";

    private static ApiClient instance;
    private ExecutorService executor; // Para rodar chamadas de rede em background
    private Handler mainHandler; // Para enviar respostas de volta para a UI (Activities)

    // Interface para "callbacks" (ouvir a resposta da API)
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // Construtor privado (padrão Singleton)
    private ApiClient() {
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Método para pegar a instância única do ApiClient
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    // --- ENDPOINTS DA API ---

    /**
     * Endpoint 1: POST /login
     * Tenta autenticar o usuário.
     */
    public void login(String email, String senha, ApiCallback<Usuario> callback) {
        executor.execute(() -> {
            try {
                // 1. Criar o JSON para enviar
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", email);
                jsonBody.put("senha", senha);

                // 2. Fazer a chamada POST
                String jsonResponse = makeRequest(API_BASE_URL + "/login", "POST", jsonBody.toString());

                // 3. Converter a resposta em um Objeto Usuario
                Usuario usuario = new Usuario(new JSONObject(jsonResponse));

                // 4. Enviar sucesso de volta para a Activity (na thread principal)
                mainHandler.post(() -> callback.onSuccess(usuario));

            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * Endpoint 2: GET /dashboard
     * Busca os números para a tela principal.
     */
    public void getDashboardStats(ApiCallback<DashboardStats> callback) {
        executor.execute(() -> {
            try {
                String jsonResponse = makeRequest(API_BASE_URL + "/dashboard", "GET", null);
                DashboardStats stats = new DashboardStats(new JSONObject(jsonResponse));
                mainHandler.post(() -> callback.onSuccess(stats));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * Endpoint 3: GET /chamados
     * Busca a lista de todos os chamados.
     */
    public void getChamados(ApiCallback<JSONArray> callback) {
        executor.execute(() -> {
            try {
                String jsonResponse = makeRequest(API_BASE_URL + "/chamados", "GET", null);
                // Retorna o Array de JSON puro para a TicketsActivity processar
                JSONArray chamadosArray = new JSONArray(jsonResponse);
                mainHandler.post(() -> callback.onSuccess(chamadosArray));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * Endpoint 4: POST /chamados
     * Cria um novo chamado. A IA (Gemini) deve ser chamada no BACK-END.
     */
    public void createChamado(String titulo, String categoria, String descricao, ApiCallback<String> callback) {
        executor.execute(() -> {
            try {
                // O App só envia o problema. O Backend define a prioridade.
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("titulo", titulo);
                jsonBody.put("categoria", categoria);
                jsonBody.put("descricao", descricao);
                // (O back-end deve saber o ID do usuário pelo Token de login)

                String jsonResponse = makeRequest(API_BASE_URL + "/chamados", "POST", jsonBody.toString());

                // Retorna uma string simples de sucesso
                mainHandler.post(() -> callback.onSuccess("Chamado criado com sucesso"));

            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // --- MOTOR DE REDE (HttpURLConnection 100% Java) ---

    /**
     * O método principal que faz a chamada de rede.
     */
    private String makeRequest(String urlString, String method, String jsonBody) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000); // 10 segundos
            conn.setReadTimeout(10000); // 10 segundos

            // Se for POST, envia o "corpo" (body)
            if (method.equals("POST") && jsonBody != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Lê a resposta
            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                return readStream(conn.getInputStream());
            } else {
                throw new IOException("Erro na API: " + code + " " + readStream(conn.getErrorStream()));
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Função auxiliar para ler a resposta da rede (InputStream).
     */
    private String readStream(InputStream stream) throws IOException {
        if (stream == null) return "Erro desconhecido";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}