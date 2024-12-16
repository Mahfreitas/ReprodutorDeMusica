package soundaura;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GerenciadorMusicas {

    @FXML
    private ListView<String> ListaMusicas;
    @FXML
    private TextField campoMusica;

    private ObservableList<String> musicas;

    public void initialize() {
        // Inicializar a lista de músicas
        musicas = FXCollections.observableArrayList();
        ListaMusicas.setItems(musicas);
    }

    @FXML
    void adicionarMusica(MouseEvent event) {
        // Abrir FileChooser para selecionar o arquivo .mp3
    FileChooser escolherMusica = new FileChooser();
    escolherMusica.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos MP3", "*.mp3"));
    
    // Mostrar o FileChooser
    File arquivoSelecionado = escolherMusica.showOpenDialog(new Stage());
    
    if (arquivoSelecionado != null) {
        // Copiar o arquivo para a pasta MinhasMusicasSoundAura
        Path destino = Paths.get(System.getProperty("user.home"), "MinhasMusicasSoundAura", arquivoSelecionado.getName());
        
        // Criar a pasta caso não exista
        File pastaDestino = new File(destino.getParent().toString());
        if (!pastaDestino.exists()) {
            pastaDestino.mkdirs();
        }
        
        try {
            Files.copy(arquivoSelecionado.toPath(), destino);
            System.out.println("Música adicionada: " + destino);
            // Agora abre a aba para o usuário inserir mais detalhes
            abrirJanelaDetalhesMusica(destino.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao copiar o arquivo.");
        }
    }
    }

    @FXML
    void irParaMusica(MouseEvent event) {
        // Pega o item selecionado na lista
        String musicaSelecionada = ListaMusicas.getSelectionModel().getSelectedItem();

        if (musicaSelecionada != null) {
            System.out.println("Você selecionou a música: " + musicaSelecionada);
            // Aqui, você pode adicionar código para tocar a música ou ir até ela de outra
            // forma
        } else {
            System.out.println("Nenhuma música selecionada.");
        }
    }

}