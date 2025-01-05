package soundaura;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Login_Controller {
    // Aqui fazemos a conexão com o nosso BD
    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    @FXML
    private Button BotaoLogin;

    @FXML
    private Hyperlink LinkLogin3;

    @FXML
    private Pane PaneTopoTela;

    @FXML
    private TextField TextFielEmailLog;

    @FXML
    private PasswordField TextFieldSenhaLog;

    @FXML
    void IrParaCad(ActionEvent event) {
        try {
            Parent Tela = FXMLLoader.load(getClass().getResource("FXMLCadastro.fxml"));
            Scene Cena = new Scene(Tela);
            Stage Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage.setScene(Cena);
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void IrParaPrincipal(ActionEvent event) {
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
    void RealizarLogin(ActionEvent event) {
        String email = TextFielEmailLog.getText();
        String senha = TextFieldSenhaLog.getText();

        try (Connection conn = connectToDatabase()) {
            String loginQuery = "SELECT id_usuario FROM usuario WHERE email_usuario = ? AND senha_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(loginQuery)) {
                stmt.setString(1, email);
                stmt.setString(2, senha);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    int idUsuario = resultSet.getInt("id_usuario");
                    SessaoUsuario.getInstancia().setIdUsuario(idUsuario);

                    System.out.println("ID configurado no login: " + SessaoUsuario.getInstancia().getIdUsuario());

                    IrParaPrincipal(event);

                    String homeDir = System.getProperty("user.home");
                    File novaPasta = new File(homeDir + File.separator + "MinhasMusicasSoundAura" + File.separator
                            + "Usuario_" + idUsuario);
                    if (!novaPasta.exists()) {
                        novaPasta.mkdir();
                    }
                } else {
                    Alert erroLogin = new Alert(Alert.AlertType.INFORMATION);
                    erroLogin.setTitle("ERRO NO LOGIN");
                    erroLogin.setHeaderText("SoundAura");
                    erroLogin.setContentText("Senha ou Email estão incorretos, tente novamente.");
                    erroLogin.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erroLogin = new Alert(Alert.AlertType.ERROR);
            erroLogin.setTitle("ERRO NO LOGIN");
            erroLogin.setHeaderText("SoundAura");
            erroLogin.setContentText("Ocorreu um erro ao tentar realizar o login.");
            erroLogin.showAndWait();
        }
    }

}
