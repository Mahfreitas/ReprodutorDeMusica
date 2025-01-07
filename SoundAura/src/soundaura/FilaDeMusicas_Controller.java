package soundaura;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FilaDeMusicas_Controller {

    @FXML
    private TableColumn<musica, String> colunaArtista;

    @FXML
    private TableColumn<musica, String> colunaTitulo;

    @FXML
    private TableView<musica> tableViewFila;

    private FilaMusicasUnica filaDeMusicas = FilaMusicasUnica.getInstancia();
    Reprodutor_Controller reprodutor = Reprodutor_Controller.getInstancia();

    private static FilaDeMusicas_Controller instancia;

    public static FilaDeMusicas_Controller getInstancia() {
        if (instancia == null) {
            instancia = new FilaDeMusicas_Controller();
        }
        return instancia;
    }

    public void initialize(){
        colunaTitulo.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));

        tableViewFila.setItems(filaDeMusicas.getFila());
    }

    @FXML
    private void removerMusica() {
        musica musicaSelecionada = tableViewFila.getSelectionModel().getSelectedItem();
        if (musicaSelecionada != null) {
            filaDeMusicas.removerMusica(musicaSelecionada);
        } else {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Aviso");
            alerta.setHeaderText(null);
            alerta.setContentText("Selecione uma música para remover.");
            alerta.showAndWait();
        }
    }

    @FXML
    void esvaziarFila(ActionEvent event) {
        filaDeMusicas.limparFila();
    }

    @FXML
    void irDireto(ActionEvent event) {
        musica musicaSelecionada = tableViewFila.getSelectionModel().getSelectedItem();
        irParaMusicaSelecionada(musicaSelecionada);
    }

    public void irParaMusicaSelecionada(musica musicaSelecionada) {
        System.out.println("Entrou aqui");
        FilaMusicasUnica fila = FilaMusicasUnica.getInstancia();
        ObservableList<musica> filaMusicas = fila.getFila();
        int index = filaMusicas.indexOf(musicaSelecionada);
        System.out.println(index);

        if (index >= 0) {
            ObservableList<musica> novaFila = FXCollections.observableArrayList(filaMusicas.subList(index, filaMusicas.size()));
            fila.setFilaDeMusicas(novaFila);
            tableViewFila.setItems(novaFila);
            tableViewFila.refresh();
            reprodutor.proxima();
        }
    }
}