package soundaura;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GestorDeTelas {
    private Stage reprodutorStage = null;
    private double eixoX = 0;
    private double eixoY = 0;
    public void abrirReprodutor() {
        if (reprodutorStage != null && reprodutorStage.isShowing()) {
            reprodutorStage.toFront();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLReprodutor.fxml"));
                loader.setController(Reprodutor_Controller.getInstance());
    
                Parent root = loader.load();
                reprodutorStage = new Stage();
                reprodutorStage.setScene(new Scene(root));
                reprodutorStage.initStyle(StageStyle.UNDECORATED);
                reprodutorStage.show();

                root.setOnMousePressed( new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent evento) {
                        eixoX = evento.getSceneX();
                        eixoY = evento.getSceneY();
                    }     
                });
                root.setOnMouseDragged(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent evento) {
                        reprodutorStage.setX(evento.getScreenX() - eixoX);
                        reprodutorStage.setY(evento.getScreenY() - eixoY);
                    }
                    
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}