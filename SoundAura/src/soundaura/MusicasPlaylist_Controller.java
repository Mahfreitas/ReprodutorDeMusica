package soundaura;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @FXML
    private Label nomePlaylist;

    private ObservableList<musica> musicas = FXCollections.observableArrayList();

    Musicas_Controller controllerMidia = new Musicas_Controller();
    GestorDeTelas gestor = new GestorDeTelas();
    Reprodutor_Controller reprodutor = Reprodutor_Controller.getInstancia();
    FilaMusicasUnica fila = FilaMusicasUnica.getInstancia();
    SessaoUsuario usuario = SessaoUsuario.getInstancia();
    GestorDeTelas gestorDeTelas = new GestorDeTelas();

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }
    
    public void initialize() {
        nomePlaylist.setText(usuario.getPlaylistAtual().getNome());
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
            String query;
            if(usuario.getPlaylistAtual().getNome().equals("Minhas Favoritas")){
                System.out.println("entrou");
                query = "SELECT * FROM musica WHERE favorita = 1 AND id_usuario = ?";

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
                        Boolean favorita = rs.getBoolean("favorita");
                        String ultimaRep = rs.getString("ultima_reproducao");
                                
                        musica novaMusica = new musica(nome, artista, album, duracao, dataAdicionada, genero, filepath, id, favorita, ultimaRep);
                        musicas.add(novaMusica);
                    }
                }
            } else {
                query = "SELECT m.* " +
                        " FROM Mplaylist mp " +
                        " JOIN musica m ON mp.musica_id = m.id_musica " +
                        " WHERE mp.playlist_id = ?";

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, usuario.getPlaylistAtual().getIdPlaylist());
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
                        String ultimaRep = rs.getString("ultima_reproducao");
                                
                        musica novaMusica = new musica(nome, artista, album, duracao, dataAdicionada, genero, filepath, id, favorita, ultimaRep);
                        musicas.add(novaMusica);
                    }
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
    void adicionarMusicaPlaylist(MouseEvent event) {
        Stage addPlayStage = null;
        try {
            // Carrega o FXML da tela da fila
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAddMsPlaylist.fxml"));
            Parent root = loader.load();
            AdicionarMusicasPlaylist_Controller controller = loader.getController();
            controller.setListaMusicas(musicas);

            addPlayStage = new Stage();
            addPlayStage.setScene(new Scene(root));
            addPlayStage.initModality(Modality.APPLICATION_MODAL);
            addPlayStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @FXML
    void adicionarMusicasSubsequentes (){
        musica musica = tabelaMusica.getSelectionModel().getSelectedItem();
        
        if (musica == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma música selecionada.");
            alerta.setContentText("Por favor, selecione uma música tocar.");
            alerta.showAndWait();
            return;
        }
        ObservableList<musica> listaMusicas = tabelaMusica.getItems();
        boolean adicionar = false;
        for (musica musica1 : listaMusicas) {
            if (adicionar) {
                fila.adicionarMusica(musica1);  // Adiciona a música à fila
            }
            if (musica.equals(musica1)) {
                adicionar = true;  // Começa a adicionar a partir da música selecionada
            }
        }
        reprodutor.tocarMusica(musica);
    }

    @FXML
    void tocarPlaylist(MouseEvent event) {
        fila.limparFila();
        for (musica musicaPlaylist : musicas) {
            fila.adicionarMusica(musicaPlaylist);
            System.out.println("Adicionada à fila: " + musicaPlaylist.getNome());
        }
        gestor.abrirReprodutor();
        reprodutor.proxima();
    }

    @FXML
    private void tocarMusicaSelecionada(MouseEvent event) {
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
        
        if (musicaSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma música selecionada.");
            alerta.setContentText("Por favor, selecione uma música para tocar.");
            alerta.showAndWait();
            return;
        }

        // "pular" as anteriores
        FilaDeMusicas_Controller filaDeMusicas = FilaDeMusicas_Controller.getInstancia();
        filaDeMusicas.irParaMusicaSelecionada(musicaSelecionada); // pular música selecionada

        reprodutor.tocarMusica(musicaSelecionada);
    }

    public TableView<musica> getTabelaMusica() {
        return tabelaMusica;
    }

    @FXML
    public void adicionarMusicaFIla(){
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
        
        if (musicaSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Nenhuma música selecionada");
            alerta.setHeaderText("Selecione uma música para adicionar na fila.");
            alerta.showAndWait();
            return;
        }
        fila.adicionarMusica(musicaSelecionada);
        
        if (fila.getFila().size() == 1 && reprodutor.getMediaPlayer() == null) {
            gestorDeTelas.abrirReprodutor();
            reprodutor.tocarMusica(musicaSelecionada); 
            fila.getFila().remove(0);
        }
        reprodutor.abrirFila();
    }

    
    @FXML
    void irParaConfiguracao(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(""));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void irParaConta(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(""));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void irParaMusicas(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLMusicas.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void irParaPlaylist(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLPlaylist.fxml")); 
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void irParaPrincipal(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLInicial.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void AbrirReprodutor(ActionEvent event) {
            gestorDeTelas.abrirReprodutor();
        }
}
