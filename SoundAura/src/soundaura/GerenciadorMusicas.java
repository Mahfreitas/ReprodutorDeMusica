package soundaura;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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
        // Pega o texto digitado no campo de texto
        String nomeMusica = campoMusica.getText();

        // Se o campo não estiver vazio, adiciona a música à lista
        if (!nomeMusica.isEmpty()) {
            musicas.add(nomeMusica);
            campoMusica.clear(); // Limpa o campo de texto após adicionar
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