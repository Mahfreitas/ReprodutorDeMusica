package soundaura;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FilaDeMusicas_Controller {

    @FXML
    private Button botaoTirarMusica;

    @FXML
    private TableColumn<musica, String> colunaArtista;

    @FXML
    private TableColumn<musica, String> colunaTitulo;

    @FXML
    private TableView<musica> tableViewFila;

    private FilaMusicasUnica filaDeMusicas = FilaMusicasUnica.getInstance();

    private static FilaDeMusicas_Controller instancia;

    public static FilaDeMusicas_Controller getInstance() {
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
    void tirarDaFila(ActionEvent event) {

    }
}