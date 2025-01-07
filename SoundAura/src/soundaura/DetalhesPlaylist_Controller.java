package soundaura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DetalhesPlaylist_Controller {

    @FXML
    private TextField campoNome;

    private ObservableList<playlist> listaPlaylist;

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    public void setListaPlaylist(ObservableList<playlist> playlists) {
        this.listaPlaylist = playlists;
    }

    @FXML
    void cancelar(MouseEvent event) {
        fecharJanela();
    }

    @FXML
    void salvarPlaylist(MouseEvent event) {
        String nome = campoNome.getText();

        if (nome.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos Incompletos");
            alerta.setHeaderText("Por favor, preencha o nome da playlist");
            alerta.showAndWait();
            return;
        }

        int idUsuario = SessaoUsuario.getInstancia().getIdUsuario();

        try{
            try (Connection conn = connectToDatabase()) {
                String verificarNome = "SELECT * FROM playlist WHERE nome_playlist = ? AND id_usuario = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(verificarNome)) {
                    checkStmt.setString(1, nome);
                    checkStmt.setInt(2, idUsuario);
                    ResultSet resultSet = checkStmt.executeQuery();
        
                    if (resultSet.next()) {
                        Alert alerta = new Alert(Alert.AlertType.WARNING);
                        alerta.setTitle("Erro");
                        alerta.setHeaderText("Nome de Playlist Duplicado");
                        alerta.setContentText("Já existe uma playlist com esse nome. Escolha outro nome.");
                        alerta.showAndWait();
                        return;
                    }
                }
                // criando a query para a adição e colocando os parametros
                String query = "INSERT INTO playlist (nome_playlist, id_usuario, duracao_total) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nome);
                    stmt.setInt(2, idUsuario);
                    stmt.setInt(3, 0);
                    stmt.executeUpdate();
                    try (ResultSet chavePrimaria = stmt.getGeneratedKeys()) {
                        if (chavePrimaria.next()) {
                            int id = chavePrimaria.getInt(1);

                            playlist novaPlaylist = new playlist(id, nome, new Timestamp(System.currentTimeMillis()).toString());
                            listaPlaylist.add(novaPlaylist);
        
                            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                            sucesso.setTitle("Sucesso");
                            sucesso.setHeaderText("Playlist Adicionada");
                            sucesso.setContentText("A playlist foi adicionada com sucesso!");
                            sucesso.showAndWait();
        
                            fecharJanela();
                        } else {
                            throw new SQLException("Falha ao obter o ID da playlist inserida.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Erro ao salvar playlist");
            erro.setContentText("Ocorreu um erro ao tentar salvar a playlist.");
            erro.showAndWait();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) campoNome.getScene().getWindow();
        stage.close();
    }
}


