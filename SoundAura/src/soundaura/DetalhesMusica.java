package soundaura;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    private File arquivoMusica; 
    
    public void configurarArquivoMusica(File arquivoMusica) {
        this.arquivoMusica = arquivoMusica;
    }

    
    @FXML
    void cancelar(MouseEvent event) {
        Stage stage = (Stage) campoNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    void salvarMusica(MouseEvent event) {
        String nome = campoNome.getText();
        String artista = campoArtista.getText();
        String album = campoAlbum.getText();
        String genero = campoGenero.getText();

        if (nome.isEmpty() || arquivoMusica == null) {
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
            String query = "INSERT INTO musicas (nome_musica, artista_musica, album_musica, genero_musica, horario_addMS, filepath_musica, id_usuario) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nome);
                stmt.setString(2, artista);
                stmt.setString(3, album);
                stmt.setString(4, genero);
                stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Horário atual
                stmt.setString(6, caminhoArquivo);
                stmt.setInt(7, idUsuario);
                stmt.executeUpdate();

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
    }
}
