package soundaura;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Playlist_Controller {

    @FXML
    private TableColumn<playlist, String> colunaData;

    @FXML
    private TableColumn<playlist, String> colunaDuracao;

    @FXML
    private TableColumn<playlist, String> colunaNome;

    @FXML
    private TableView<playlist> tabelaPlaylist;
    private ObservableList<playlist> playlists = FXCollections.observableArrayList();
    int idUsuarioAtual = SessaoUsuario.getInstancia().getIdUsuario();
    

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    @FXML
    public void initialize(){
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        colunaDuracao.setCellValueFactory(new PropertyValueFactory<>("duracaoTotal"));

        // basicamente deixando o nome da playlist editavel
        colunaNome.setCellFactory(TextFieldTableCell.forTableColumn());
        colunaNome.setOnEditCommit(event -> {
            playlist playlistEditada = event.getRowValue();
            String novoNome = event.getNewValue();

            if (novoNome.isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Erro");
                alerta.setHeaderText("Nome Vazio");
                alerta.setContentText("O nome da playlist não pode estar vazio.");
                alerta.showAndWait();
                tabelaPlaylist.refresh();
            } else {
                playlistEditada.setNome(novoNome);
                atualizarPlaylistNoBanco(playlistEditada);
            }
    });

    tabelaPlaylist.setEditable(true);
        tabelaPlaylist.setItems(playlists);
        carregarPlaylistsDoBanco();
    }

    public void carregarPlaylistsDoBanco() {
        playlists.clear();
        try (Connection conn = connectToDatabase()) {
            String query = "SELECT * FROM playlist WHERE id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuarioAtual);
                var rs = stmt.executeQuery();
                playlists.clear();
                while (rs.next()) {
                    String nome = rs.getString("nome_playlist");
                    Integer duracao = rs.getInt("duracao_total");
                    String dataAdicionada = rs.getString("horario_addPS");
                    Integer id = rs.getInt("id_playlist");
                    
                    playlist novaPlaylist = new playlist(id, nome, dataAdicionada, duracao);
                    playlists.add(novaPlaylist);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro ao carregar músicas");
            erro.setHeaderText("Erro ao carregar músicas do banco de dados.");
            erro.showAndWait();
        }
    }

    private void atualizarPlaylistNoBanco(playlist playlistEditada) {
        try{
            try (Connection conn = connectToDatabase()) {
                String query = "UPDATE playlist SET nome_playlist = ? WHERE id_playlist = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, playlistEditada.getNome());
                    stmt.setInt(2, playlistEditada.getIdPlaylist());
                    stmt.executeUpdate();

                    Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                    sucesso.setTitle("Sucesso");
                    sucesso.setHeaderText("Playlist Editada");
                    sucesso.setContentText("A playlist teve seu nome alterado com sucesso!");
                    sucesso.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Erro ao editar playlist");
            erro.setContentText("Ocorreu um erro ao tentar editar a playlist.");
            erro.showAndWait();
        }
    }

    @FXML
    void adicionarPlaylist(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDetalhesPlaylist.fxml"));
            Parent root = loader.load();

            DetalhesPlaylist_Controller controller = loader.getController();
            controller.setListaPlaylist(playlists); // Passar o ObservableList

            Stage detalhesStage = new Stage();
            detalhesStage.setScene(new Scene(root));
            detalhesStage.setTitle("Detalhes da Música");
            detalhesStage.initModality(Modality.APPLICATION_MODAL);
            detalhesStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void apagarPlaylist(MouseEvent event) {
        playlist playlistSelecionada = tabelaPlaylist.getSelectionModel().getSelectedItem();
        
        if (playlistSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma playlist selecionada.");
            alerta.setContentText("Por favor, selecione uma playlist para excluir.");
            alerta.showAndWait();
            return;
        }


        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Tem certeza que deseja excluir esta playlist?");
        confirmacao.setContentText("Essa ação não pode ser desfeita.");
        
        if (confirmacao.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
            // apagar da ListView
            tabelaPlaylist.getItems().remove(playlistSelecionada);

            // apagar do banco de dados
            try (Connection conn = connectToDatabase()) {
                String query = "DELETE FROM playlist WHERE id_playlist = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, playlistSelecionada.getIdPlaylist());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro ao Apagar Playlist");
                    erro.setHeaderText("Erro ao apagar a playlist do banco de dados.");
                    erro.showAndWait();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro ao acessar playlist");
                erro.setHeaderText("Erro ao acessar playlist do banco de dados.");
                erro.showAndWait();
            }

            // Mensagem de sucesso
            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Playlist Excluída");
            sucesso.setHeaderText("A playlist foi excluída com sucesso.");
            sucesso.showAndWait();
        }
    }   

    @FXML
    void abrirPlaylist(MouseEvent event) {
        playlist playlistSelecionada = tabelaPlaylist.getSelectionModel().getSelectedItem();

        if (playlistSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma playlist selecionada.");
            alerta.setContentText("Por favor, selecione uma playlist para abrir.");
            alerta.showAndWait();
            return;
        }

        MusicasPlaylist_Controller playcontroller = new MusicasPlaylist_Controller();
        SessaoUsuario.getInstancia().setPlaylistAtual(playlistSelecionada);

        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLPlaylistMusicas.FXML"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}