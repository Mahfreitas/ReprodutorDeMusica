package soundaura;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class DetalhesMusica_Controller {
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
    private ObservableList<musica> listaMusicas;

    public void configurarArquivoMusica(File arquivo, String duracao) {
        this.arquivoMusica = arquivo;
        this.duracaoMusica = duracao;
        campoDuracao.setText(duracao);
        campoDuracao.setEditable(false);
    }

    public void setListaMusicas(ObservableList<musica> listaMusicas) {
        this.listaMusicas = listaMusicas;
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
            alerta.setHeaderText("Por favor, preencha todos os campos");
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
            // não esqueca de colocar uma funcção que feche a pagina e va para a parte de login para reinicializar a parte da sessão do usuario
            return;
        }
        try{
            // adicionando a musica nova - com um nome padrão de adição e criando as pastas caso não tenha sido criada por algum motivo
            Path destino = Paths.get(System.getProperty("user.home"), "MinhasMusicasSoundAura", "Usuario_" + idUsuario, "NovaMusica003.mp3");
            File pastaDestino = new File(destino.getParent().toString());
            if (!pastaDestino.exists()) {
                pastaDestino.mkdirs();
            }
            // copiando o arquivo raiz para a pasta raiz, no intuito de manter os arquivos sempre salvos caso ocorrra algo com o arquivo raiz
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
            // pegar o caminho do arquivo copiado
            String caminhoArqCopiado = destino.toString();

            try (Connection conn = connectToDatabase()) {
                // criando a query para a adição e colocando os parametros
                String query = "INSERT INTO musica (nome_musica, artistas_musica, album_musica, genero_musica, horario_addMS, filepath_musica, id_usuario, duracao_musica) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nome);
                    stmt.setString(2, artista);
                    stmt.setString(3, album);
                    stmt.setString(4, genero);
                    stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    stmt.setString(6, caminhoArqCopiado);
                    stmt.setInt(7, idUsuario);
                    stmt.setString(8, duracaoMusica);
                    stmt.executeUpdate();
                    try (ResultSet chavePrimariaMusica = stmt.getGeneratedKeys()) {
                        if (chavePrimariaMusica.next()) {
                            // pegando o id da musica para alterar o nome na pasta raiz e modificar seu filepath no banco de dados
                            int idMusica = chavePrimariaMusica.getInt(1);

                             // renomear o arquivo com o ID da música no banco mantendo o caminho no diretorio
                            Path destinoModificado = destino.resolveSibling(idMusica + ".mp3");
                            Files.move(destino, destinoModificado);

                            // atualiza o caminho no banco para refletir o ID correto da musica, para permitir a funcionalidade de play
                            String updateQuery = "UPDATE musica SET filepath_musica = ? WHERE id_musica = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, destinoModificado.toString());
                                updateStmt.setInt(2, idMusica);
                                updateStmt.executeUpdate();
                            }
                            // adicionar a musica na lista para aparecer no View
                            musica novaMusica = new musica(nome, artista, album, duracaoMusica, new Timestamp(System.currentTimeMillis()).toString(), genero, destinoModificado.toString(), idMusica);
                            listaMusicas.add(novaMusica);
        
                            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                            sucesso.setTitle("Sucesso");
                            sucesso.setHeaderText("Música Adicionada");
                            sucesso.setContentText("A música foi adicionada com sucesso!");
                            sucesso.showAndWait();
    
                            // Fechar a janela após salvar
                            fecharJanela();
                        } else {
                            throw new SQLException("Falha ao obter o ID da música inserida.");
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Erro ao salvar música");
            erro.setContentText("Ocorreu um erro ao tentar salvar a música.");
            erro.showAndWait();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) campoNome.getScene().getWindow();
        stage.close();
    }

}
