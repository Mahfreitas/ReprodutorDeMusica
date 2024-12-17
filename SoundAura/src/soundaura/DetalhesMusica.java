package soundaura;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DetalhesMusica {
    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    @FXML
    private TextField campoAlbum;

    @FXML
    private TextField campoArtista;

    @FXML
    private TextField campoGenero;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoDuracao;

    private File arquivoMusica;
    private String duracaoMusica;
    GerenciadorMusicas gerenciadorMusicas = new GerenciadorMusicas();

    public void configurarArquivoMusica(File arquivo, String duracao) {
        this.arquivoMusica = arquivo;
        this.duracaoMusica = duracao;

        // Preencher o campo de duração
        campoDuracao.setText(duracao);
        campoDuracao.setEditable(false); // Tornar o campo de duração apenas leitura
    }

    @FXML
    void cancelar(MouseEvent event) {
        fecharJanela();
    }

    @FXML
    void salvarMusica(MouseEvent event) {
        String nome = campoNome.getText();
        String artista = campoArtista.getText();
        String album = campoAlbum.getText();
        String genero = campoGenero.getText();

        if (nome.isEmpty() || artista.isEmpty() || album.isEmpty() || genero.isEmpty() || arquivoMusica == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos Incompletos");
            alerta.setHeaderText("Por favor, preencha pelo menos o campo com o nome da música.");
            alerta.showAndWait();
            return;
        }

        int idUsuario = SessaoUsuario.getInstancia().getIdUsuario();
        if (idUsuario == 0) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro de Sessão");
            alerta.setHeaderText("Nenhum usuário logado.");
            alerta.setContentText("Por favor, faça login novamente.");
            alerta.showAndWait();
            return;
        }

        String caminhoArquivo = arquivoMusica.getAbsolutePath();

        try (Connection conn = connectToDatabase()) {
            String query = "INSERT INTO musica (nome_musica, artistas_musica, album_musica, genero_musica, horario_addMS, filepath_musica, id_usuario, duracao_musica) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nome);
                stmt.setString(2, artista);
                stmt.setString(3, album);
                stmt.setString(4, genero);
                stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Horário atual
                stmt.setString(6, caminhoArquivo);
                stmt.setInt(7, idUsuario);
                stmt.setString(8, duracaoMusica);
                stmt.executeUpdate();

                Path destino = Paths.get(System.getProperty("user.home"), "MinhasMusicasSoundAura",
                        arquivoMusica.getName());
                File pastaDestino = new File(destino.getParent().toString());
                if (!pastaDestino.exists()) {
                    pastaDestino.mkdirs();
                }

                try {
                    Files.copy(arquivoMusica.toPath(), destino);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro ao copiar o arquivo");
                    erro.setHeaderText("Ocorreu um erro ao copiar o arquivo.");
                    erro.setContentText("Por favor, tente novamente.");
                    erro.showAndWait();
                    return;
                }

                gerenciadorMusicas.carregarMusicasDoBanco();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText("Música Adicionada");
                sucesso.setContentText("A música foi adicionada com sucesso!");
                sucesso.showAndWait();

                // Fechar a janela após salvar
                fecharJanela();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Erro ao salvar música");
            erro.setContentText("Ocorreu um erro ao tentar salvar a música no banco de dados.");
            erro.showAndWait();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) campoNome.getScene().getWindow();
        stage.close();
    }
}
