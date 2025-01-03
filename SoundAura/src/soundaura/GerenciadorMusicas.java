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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GerenciadorMusicas {

    @FXML
    private TextField campoMusica;

    @FXML
    private TableColumn<Musica, String> colunaAlbum;

    @FXML
    private TableColumn<Musica, String> colunaArtista;

    @FXML
    private TableColumn<Musica, String> colunaData;

    @FXML
    private TableColumn<Musica, String> colunaDuracao;

    @FXML
    private TableColumn<Musica, String> colunaNome;
    @FXML
    private TableView<Musica> tabelaMusica;
    private ObservableList<Musica> musicas;

    int idUsuarioAtual = SessaoUsuario.getInstancia().getIdUsuario();
    
    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

     public void initialize() {
        
        musicas = FXCollections.observableArrayList();
        tabelaMusica.setItems(musicas);

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colunaAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        colunaDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataAdicionada"));

        carregarMusicasDoBanco();
    }

    public void carregarMusicasDoBanco() {
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

                    Musica musica = new Musica(nome, artista, album, genero, duracao, filepath, dataAdicionada);
                    musicas.add(musica);
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

    @FXML
    void abrirJanelaDetalhesMusica(File arquivoSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PaginaDetalhesMusica.fxml"));
            Parent root = loader.load();

            DetalhesMusica controller = loader.getController();
            Media media = new Media(arquivoSelecionado.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                double duracaoSegundos = media.getDuration().toSeconds();
                String duracaoFormatada = formatarDuracao(duracaoSegundos);

                controller.configurarArquivoMusica(arquivoSelecionado, duracaoFormatada);
            });

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
    void adicionarMusica(MouseEvent event) {
        System.out.println("ID do Usuário Atual111: " + idUsuarioAtual);
        FileChooser escolherMusica = new FileChooser();
        escolherMusica.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos MP3", "*.mp3"));

        File arquivoSelecionado = escolherMusica.showOpenDialog(new Stage());

        if (arquivoSelecionado != null) {
            abrirJanelaDetalhesMusica(arquivoSelecionado);
        }

    }

    private String formatarDuracao(double duracaoSegundos) {
        int minutos = (int) (duracaoSegundos / 60);
        int segundos = (int) (duracaoSegundos % 60);
        return String.format("%02d:%02d", minutos, segundos);
    }
    
    @FXML
    void irParaMusica(MouseEvent event) {

    }

}