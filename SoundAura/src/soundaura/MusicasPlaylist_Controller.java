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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MusicasPlaylist_Controller {

    @FXML
    private TableColumn<musica, String> colunaAlbum;

    @FXML
    private TableColumn<musica, String> colunaArtista;

    @FXML
    private TableColumn<musica, String> colunaData;

    @FXML
    private TableColumn<musica, String> colunaDuracao;

    @FXML
    private TableColumn<musica, String> colunaNome;

    @FXML
    private ImageView imagemTocar;

    @FXML
    private ImageView imagemTocar1;

    @FXML
    private TableView<musica> tabelaMusica;

    private ObservableList<musica> musicas = FXCollections.observableArrayList();
    Musicas_Controller controllerMidia = new Musicas_Controller();
    GestorDeTelas gestor = new GestorDeTelas();
    Reprodutor_Controller reprodutor = Reprodutor_Controller.getInstance();
    SessaoUsuario usuario = SessaoUsuario.getInstancia();

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    private boolean removerMusicaDoBanco(musica musicaSelecionada) {
        // Conectar ao banco e remover a música da playlist (dependendo da estrutura do banco)
        try (Connection conn = connectToDatabase()) {
            String query = "DELETE FROM Mplaylist WHERE musica_id = ? AND playlist_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, musicaSelecionada.getId()); // ID da música
                stmt.setInt(2, usuario.getPlaylistAtual().getIdPlaylist()); // ID da playlist atual
                int resultado = stmt.executeUpdate();
                
                return resultado > 0; // Retorna true se a música foi removida
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void initialize() {
        configurarTabela();
        carregarMusicasDoBanco();
    }

    public void configurarTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colunaAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        colunaDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataAdicionada"));

        tabelaMusica.setItems(musicas);
    }

    public void carregarMusicasDoBanco() {
        musicas.clear();
        try (Connection conn = connectToDatabase()) {
            String query = "SELECT * FROM musica WHERE id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, usuario.getIdUsuario());
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

                    
                    musica novaMusica = new musica(nome, artista, album, duracao, dataAdicionada, genero, filepath, id);
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
    void abrirReprodutor(MouseEvent event) {

    }

    @FXML
    void adicionarFila(MouseEvent event) {
        
    }

    @FXML
    void adicionarMusicaPlaylist(MouseEvent event) {
        gestor.addPlayStage();
    }

    @FXML
    void apagarMusicaPlaylist(MouseEvent event) {
         // Obter a música selecionada na tabela
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
        
        if (musicaSelecionada == null) {
            // Caso nenhuma música seja selecionada, exibe um alerta
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma música selecionada.");
            alerta.setContentText("Por favor, selecione uma música para remover.");
            alerta.showAndWait();
            return;
        }
        
        // Remover a música da playlist no banco de dados
        boolean sucesso = removerMusicaDoBanco(musicaSelecionada);
        
        if (sucesso) {
            // Remover da tabela, caso a remoção no banco tenha sido bem-sucedida
            tabelaMusica.getItems().remove(musicaSelecionada);
            
            // Mostrar mensagem de sucesso
            Alert sucessoAlerta = new Alert(Alert.AlertType.INFORMATION);
            sucessoAlerta.setTitle("Música removida");
            sucessoAlerta.setHeaderText("Música removida com sucesso.");
            sucessoAlerta.setContentText("A música foi removida da playlist.");
            sucessoAlerta.showAndWait();
        } else {
            // Caso ocorra algum erro ao remover a música
            Alert erroAlerta = new Alert(Alert.AlertType.ERROR);
            erroAlerta.setTitle("Erro ao remover música");
            erroAlerta.setHeaderText("Não foi possível remover a música.");
            erroAlerta.setContentText("Houve um erro ao tentar remover a música da playlist.");
            erroAlerta.showAndWait();
        }
    }

    @FXML
    void tocar(MouseEvent event) {

    }

    @FXML
    void tocarPlaylist(MouseEvent event) {

    }

}
