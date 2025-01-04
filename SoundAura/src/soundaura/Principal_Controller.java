package soundaura;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Principal_Controller {
    @FXML
    void IrParaPrincipal(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLInicial.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void IrParaPlaylist(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource(""));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void IrParaMusicas(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLMusicas.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void IrParaConfiguracoes(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource(""));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void IrParaConta(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource(""));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void Sair(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void trocarImage(MouseEvent event) {

    }

}
