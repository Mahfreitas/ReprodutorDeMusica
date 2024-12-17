package soundaura;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage stage;
    private static Scene PaginaLogin;
    private static Scene PaginaCadastro;
    private static Scene PaginaPrimaria;
    private static Scene PaginaMusicas;

    @Override
    public void start(Stage primaryStage) throws IOException { 
        stage = primaryStage;
        primaryStage.setTitle("Inicio");

        Parent fxmlLogin = FXMLLoader.load(getClass().getResource("PaginaLogin.fxml"));
        PaginaLogin = new Scene(fxmlLogin, 640, 400);

        Parent fxmlCad = FXMLLoader.load(getClass().getResource("PaginaCadastro.fxml"));
        PaginaCadastro = new Scene(fxmlCad, 640, 400);

        Parent fxmlPrim = FXMLLoader.load(getClass().getResource("TelaPrimaria.fxml"));
        PaginaPrimaria = new Scene(fxmlPrim, 640, 400);

        Parent fxmlMusic = FXMLLoader.load(getClass().getResource("PaginaMusicas.fxml"));
        PaginaMusicas = new Scene(fxmlMusic, 640, 400);

        primaryStage.setScene(PaginaLogin);
        primaryStage.show();
    }

    public static void alterarTelas(String NomeTela){
        switch(NomeTela){
            case "Login":
                stage.setScene(PaginaLogin);
                break;
            case "Cadastro":
                stage.setScene(PaginaCadastro);
                break;
            case "PaginaPrimaria":
                stage.setScene(PaginaPrimaria);
                break;
            case "Musicas":
                stage.setScene(PaginaMusicas);
                break;
            case "Playlist":
                stage.setScene(PaginaPrimaria);
                break;
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
