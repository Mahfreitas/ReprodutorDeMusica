package soundaura;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GestorDeTelas {
    public void abrirReprodutor() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLReprodutor.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Reprodutor de Música");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}