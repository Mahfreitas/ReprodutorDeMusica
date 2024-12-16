package soundaura;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class TelaPrimaria {

    @FXML
    void trocarImage(MouseEvent event) {

    }

    @FXML
    void irParaMusica(MouseEvent event) {
        Main.alterarTelas("Musicas");
    }

}
