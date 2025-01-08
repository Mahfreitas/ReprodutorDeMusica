package soundaura;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Principal_Controller {
    @FXML
    private TableColumn<musica, String> artista;

    @FXML
    private TableColumn<musica, String> nome;

    @FXML
    private TableColumn<musica, String> ultimaRep;

    @FXML
    private TableView<musica> tabelaRep;

    SessaoUsuario usuario = SessaoUsuario.getInstancia();
    GestorDeTelas gestorDeTelas = new GestorDeTelas();
    
    @FXML
    public void initialize() {
        nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        artista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        ultimaRep.setCellValueFactory(new PropertyValueFactory<>("ultimaRep"));

        carregarHistorico();
    }

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    public void carregarHistorico(){
            tabelaRep.getItems().clear();
            try (Connection conn = connectToDatabase()) {
                String query = "SELECT * FROM musica WHERE ultima_reproducao IS NOT NULL AND id_usuario = ? ORDER BY ultima_reproducao DESC";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, usuario.getIdUsuario());
                    var rs = stmt.executeQuery();
                    while (rs.next()) {
                        String nome = rs.getString("nome_musica");
                        String artista = rs.getString("artistas_musica");
                        String album = rs.getString("album_musica");
                        String genero = rs.getString("genero_musica");
                        String duracao = rs.getString("duracao_musica");
                        String filepath = rs.getString("filepath_musica");
                        String dataAdicionada = rs.getString("horario_addMS");
                        Integer id = rs.getInt("id_musica");
                        Boolean favorita = rs.getBoolean("favorita");
                        String ultimaRep = rs.getString("ultima_reproducao");
                        
                        musica novaMusica = new musica(nome, artista, album, duracao, dataAdicionada, genero, filepath, id, favorita, ultimaRep);
                        tabelaRep.getItems().add(novaMusica);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro ao carregar histórico");
                erro.setHeaderText("Erro ao carregar o histórico de reprodução.");
                erro.showAndWait();
            }
        }

    @FXML
    public void IrParaPrincipal(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        carregarHistorico();
    }
    @FXML
    public void IrParaPlaylist(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLPlaylist.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void IrParaMusicas(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLMusicas.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void IrParaConfiguracoes(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource(""));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void IrParaConta(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource(""));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void Sair(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
            Scene Cena = new Scene(Tela);
            usuario.encerrarSessao();
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void AbrirReprodutor(ActionEvent event) {
            gestorDeTelas.abrirReprodutor();
        }

}
