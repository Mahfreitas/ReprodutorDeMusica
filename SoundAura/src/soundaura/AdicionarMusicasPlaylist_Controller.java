package soundaura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdicionarMusicasPlaylist_Controller {

    @FXML
    private TableColumn<musica, String> colunaNome;

    @FXML
    private TableView<musica> tabelaMusica;

    private ObservableList<musica> listaMusicas;
    
    public void setListaMusicas(ObservableList<musica> listaMusicas) {
        this.listaMusicas = listaMusicas;
    }

    private ObservableList<musica> musicas = FXCollections.observableArrayList();
    int idUsuarioAtual = SessaoUsuario.getInstancia().getIdUsuario();

    public void initialize() {
        configurarTabela();
        carregarMusicasDoBanco();
    }

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    private void configurarTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        tabelaMusica.setItems(musicas);
    }

    public void carregarMusicasDoBanco() {
        musicas.clear();
        try (Connection conn = connectToDatabase()) {
            String query = "SELECT * FROM musica WHERE id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuarioAtual);
                var rs = stmt.executeQuery();
                musicas.clear();
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
                    musica novaMusica = new musica(nome, artista, album, duracao, dataAdicionada, genero, filepath, id, favorita);
                    musicas.add(novaMusica);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro ao carregar músicas");
            erro.setHeaderText("Erro ao carregar músicas do banco de dados.");
            erro.showAndWait();
        }
        tabelaMusica.setItems(musicas);
        // aqui nos vamos fazer o tratamento caso o usuario logue em outro dispositivo e tente rodar uma musica que não está disponivel no novo dispositivo
        tabelaMusica.setRowFactory(param -> new TableRow<musica>() {
            @Override
            protected void updateItem(musica musica, boolean vazio) {
                // aqui utilizamos um super pois ela chama da classe Table a função padrao, assim mantendo os textos e conteudos da tabela e executando somente a mudança da cor e o disable
                super.updateItem(musica, vazio);
                // um tratamento para saber se a celula é vazia
                if (musica == null || vazio) {
                    setStyle("");
                    setDisable(false);
                } else {
                    if (!musica.arquivoDisponivel()) {
                        setDisable(true);
                        setStyle("-fx-background-color: gray;");
                    } else if (musica.isFavorita()) {
                        setDisable(false);  
                        setStyle("-fx-background-color: #ef5350");
                    } else {
                        setDisable(false);  
                        setStyle("");
                    }
                }
            }
        });
    }
    
    @FXML
    public void adicionarMusicaNaPlaylist1() {
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
        if (musicaSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma música selecionada.");
            alerta.setContentText("Por favor, selecione uma música.");
            alerta.showAndWait();
            return;
        }

        int idPlaylist = SessaoUsuario.getInstancia().getPlaylistAtual().getIdPlaylist();
        String query = "INSERT INTO Mplaylist (musica_id, playlist_id) VALUES (?, ?)";
        try (Connection conn = connectToDatabase();){
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, musicaSelecionada.getId());
                stmt.setInt(2, idPlaylist);
                stmt.executeUpdate();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText("Música Adicionada");
                sucesso.setContentText("A música foi adicionada com sucesso!");
                sucesso.showAndWait();

                listaMusicas.add(musicaSelecionada);
                
                fecharCena();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Erro ao salvar música");
            erro.setContentText("Ocorreu um erro ao tentar salvar a música.");
            erro.showAndWait();
        }
    }
    
    @FXML
    void fecharCena() {
        Stage stageAtual = (Stage) tabelaMusica.getScene().getWindow();
        stageAtual.close();
    }
}
