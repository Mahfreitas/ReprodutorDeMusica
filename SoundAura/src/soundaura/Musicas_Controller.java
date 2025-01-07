package soundaura;

import java.io.File;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Musicas_Controller {
    @FXML
    private TextField campoMusica;

    @FXML
    private ImageView imagemTocar;

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
    private TableView<musica> tabelaMusica;

    private ObservableList<musica> musicas = FXCollections.observableArrayList();

    int idUsuarioAtual = SessaoUsuario.getInstancia().getIdUsuario();
    Reprodutor_Controller reprodutor = Reprodutor_Controller.getInstancia();
    private FilaMusicasUnica filaDeMusicas = FilaMusicasUnica.getInstancia();
    GestorDeTelas gestorDeTelas = new GestorDeTelas();
    
    

    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    private String formatarDuracao(double duracaoSegundos) {
        int minutos = (int) (duracaoSegundos / 60);
        int segundos = (int) (duracaoSegundos % 60);
        return String.format("%02d:%02d", minutos, segundos);
    }

    private void configurarDuploClique() {
        tabelaMusica.setOnMouseClicked(event -> {
            if ((event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)) {
                musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
                if (musicaSelecionada != null) {
                    gestorDeTelas.abrirReprodutor();
                    reprodutor.tocarMusica(musicaSelecionada);
                }
            }
        });
    }

    public void initialize() {
        configurarTabela();
        carregarMusicasDoBanco();
        configurarDuploClique();
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
                    String ultimaRep = rs.getString("ultima_reproducao");
                    
                    musica novaMusica = new musica(nome, artista, album, duracao, dataAdicionada, genero, filepath, id, favorita, ultimaRep);
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
    void abrirJanelaDetalhesMusica(File arquivoSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDetalhesMusica.fxml"));
            Parent root = loader.load();

            DetalhesMusica_Controller controller = loader.getController();
            controller.setListaMusicas(musicas); // Passar o ObservableList

            Media media = new Media(arquivoSelecionado.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                double duracaoSegundos = media.getDuration().toSeconds();
                String duracaoFormatada = formatarDuracao(duracaoSegundos);

                controller.configurarArquivoMusica(arquivoSelecionado, duracaoFormatada);

                Stage detalhesStage = new Stage();
                detalhesStage.setScene(new Scene(root));
                detalhesStage.setTitle("Detalhes da Música");
                detalhesStage.initModality(Modality.APPLICATION_MODAL);
                detalhesStage.showAndWait();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void adicionarMusica(MouseEvent event) {
        System.out.println("ID do Usuário Atual111: " + idUsuarioAtual);
        FileChooser escolherMusica = new FileChooser();
        
        escolherMusica.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos MP3", "*.mp3"));

        File arquivoSelecionado = escolherMusica.showOpenDialog(new Stage());

        if (arquivoSelecionado != null) {
            abrirJanelaDetalhesMusica(arquivoSelecionado);
        }

    }
    
    @FXML
    void apagarMusica(MouseEvent event) {
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
        
        if (musicaSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Seleção Inválida");
            alerta.setHeaderText("Nenhuma música selecionada.");
            alerta.setContentText("Por favor, selecione uma música para excluir.");
            alerta.showAndWait();
            return;
        }


        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Tem certeza que deseja excluir esta música?");
        confirmacao.setContentText("Essa ação não pode ser desfeita.");
        
        if (confirmacao.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
            // apagar da ListView
            tabelaMusica.getItems().remove(musicaSelecionada);

            // apagar do banco de dados
            try (Connection conn = connectToDatabase()) {
                String query = "DELETE FROM musica WHERE id_musica = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, musicaSelecionada.getId());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro ao Apagar Música");
                    erro.setHeaderText("Erro ao apagar a música do banco de dados.");
                    erro.showAndWait();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro ao acessar musicas");
                erro.setHeaderText("Erro ao acessar música do banco de dados.");
                erro.showAndWait();
            }

            // apagar o arquivo da pasta raiz
            File arquivoMusica = new File(musicaSelecionada.getFilepath());
            if (arquivoMusica.exists()) {
                if (arquivoMusica.delete()) {
                    System.out.println("Arquivo da música excluído com sucesso.");
                } else {
                    System.out.println("Falha ao excluir o arquivo da música.");
                }
            }

            // Mensagem de sucesso
            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Música Excluída");
            sucesso.setHeaderText("A música foi excluída com sucesso.");
            sucesso.showAndWait();
        }
    }

    @FXML
    void marcarDesmarcarFavorito(MouseEvent event) {
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();

        if (musicaSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Nenhuma música selecionada");
            alerta.setHeaderText("Selecione uma música para adicionar na fila.");
            alerta.showAndWait();
            return;
        }
        try (Connection conn = connectToDatabase()) {
            String query = "UPDATE musica SET favorita = ? WHERE id_musica = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                boolean novaCondicaoFavorita = !musicaSelecionada.isFavorita();
                stmt.setBoolean(1, novaCondicaoFavorita);
                stmt.setInt(2, musicaSelecionada.getId());
                stmt.executeUpdate();
    
                musicaSelecionada.setFavorita(novaCondicaoFavorita);
                tabelaMusica.refresh(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");
            alerta.setHeaderText("Erro ao alterar status de favorita");
            alerta.setContentText("Ocorreu um erro ao tentar marcar ou desmarcar a música como favorita.");
            alerta.showAndWait();
        }
    }

    @FXML
    void abrirReprodutor(MouseEvent event) {
        gestorDeTelas.abrirReprodutor();
    }

    @FXML
    void abrirFila(MouseEvent event) {
        reprodutor.abrirFila();
    }

    @FXML
    void adicionarFila(MouseEvent event) {
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();

        if (musicaSelecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Nenhuma música selecionada");
            alerta.setHeaderText("Selecione uma música para adicionar na fila.");
            alerta.showAndWait();
            return;
        }
        filaDeMusicas.adicionarMusica(musicaSelecionada);
        
        if (filaDeMusicas.getFila().size() == 1 && reprodutor.getMediaPlayer() == null) {
            gestorDeTelas.abrirReprodutor();
            reprodutor.tocarMusica(musicaSelecionada);  // Toca a música automaticamente
            filaDeMusicas.getFila().remove(0);
        }
        reprodutor.abrirFila();
    }

    @FXML
    void tocar(MouseEvent event){
        musica musicaSelecionada = tabelaMusica.getSelectionModel().getSelectedItem();
        if (musicaSelecionada != null) {
            gestorDeTelas.abrirReprodutor();
            reprodutor.tocarMusica(musicaSelecionada);
        } else {
            if (musicaSelecionada == null) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Nenhuma música selecionada");
                alerta.setHeaderText("Selecione uma música para toocar.");
                alerta.showAndWait();
                return;
            }
        }
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
    void irParaReprodutor(MouseEvent event) {
        gestorDeTelas.abrirReprodutor();
    }
}