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

public class Cadastro_Controller {
    // Aqui fazemos a conexão com o nosso BD
    private static Connection connectToDatabase() throws SQLException {
        String url = MySQL.getUrl();
        String user = MySQL.getUser();
        String password = MySQL.getPassword();

        return DriverManager.getConnection(url, user, password);
    }

    @FXML
    private Button BotaoCadastro;

    @FXML
    private Hyperlink LinkLogin2;

    @FXML
    private Pane PaneTopoTela;

    @FXML
    private TextField TextFielEmailCad;

    @FXML
    private TextField TextFieldConfSenhaCad;

    @FXML
    private PasswordField TextFieldSenhaCad;

    @FXML
    void IrParaLogin(ActionEvent event) {
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
    void RealizarCadastro(ActionEvent event) {
        String email = TextFielEmailCad.getText();
        String senha = TextFieldSenhaCad.getText();
        String confSenha = TextFieldConfSenhaCad.getText();

        if (!email.isEmpty() && !senha.isEmpty() && !confSenha.isEmpty()) {
            if (senha.equals(confSenha)) {
                try (Connection conn = connectToDatabase()) {
                    String checkQuery = "SELECT * FROM usuario WHERE email_usuario = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                        checkStmt.setString(1, email);
                        ResultSet resultSet = checkStmt.executeQuery();
                        if (resultSet.next()) {
                            Alert cadastroErro = new Alert(Alert.AlertType.INFORMATION);
                            cadastroErro.setTitle("ERRO NO CADASTRO");
                            cadastroErro.setHeaderText("SoundAura");
                            cadastroErro.setContentText("O usuário em questão já existe, tente novamente");
                            cadastroErro.showAndWait();
                        } else {
                            if (!email.contains("@")) {
                                Alert cadastroErro2 = new Alert(Alert.AlertType.INFORMATION);
                                cadastroErro2.setTitle("ERRO NO CADASTRO");
                                cadastroErro2.setHeaderText("SoundAura");
                                cadastroErro2.setContentText("O email é inválido, tente novamente");
                                cadastroErro2.showAndWait();
                            } else {
                                String insertQuery = "INSERT INTO usuario (email_usuario, senha_usuario) VALUES (?, ?)";
                                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                    insertStmt.setString(1, email);
                                    insertStmt.setString(2, senha);
                                    insertStmt.executeUpdate();

                                    Alert cadastroRealizado = new Alert(Alert.AlertType.INFORMATION);
                                    cadastroRealizado.setTitle("Cadastro Realizado com Sucesso");
                                    cadastroRealizado.setHeaderText("SoundAura");
                                    cadastroRealizado.setContentText("O seu cadastro foi realizado com sucesso");
                                    cadastroRealizado.showAndWait();

                                    try (ResultSet chave_Primaria = insertStmt.getGeneratedKeys()) {
                                        if (chave_Primaria.next()) {
                                            int idUsuario = chave_Primaria.getInt(1);
                                            SessaoUsuario.getInstancia().setIdUsuario(idUsuario);
                                            System.out.println("ID configurado no login: " + SessaoUsuario.getInstancia().getIdUsuario());
                                            String homeDir = System.getProperty("user.home");
                                            File novaPasta = new File(homeDir + File.separator + "MinhasMusicasSoundAura" + File.separator + "Usuario_" + idUsuario);
                                            if (!novaPasta.exists()) {
                                                novaPasta.mkdir();
                                            }

                                            String insertPlaylistQuery = "INSERT INTO playlists (nome_playlist, usuario_id) VALUES (?, ?)";
                                            try (PreparedStatement playlistStmt = conn.prepareStatement(insertPlaylistQuery)) {
                                                playlistStmt.setString(1, "Minhas Favoritas");
                                                playlistStmt.setInt(2, idUsuario);
                                                playlistStmt.executeUpdate();
                                            }
                                        } else {
                                            throw new SQLException("Erro ao obter o ID do usuário inserido.");
                                        }
                                    }
                                    IrParaPrincipal(event);
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert erroCadastro = new Alert(Alert.AlertType.ERROR);
                    erroCadastro.setTitle("ERRO NO CADASTRO");
                    erroCadastro.setHeaderText("SoundAura");
                    erroCadastro.setContentText("Ocorreu um erro ao tentar realizar o cadastro.");
                    erroCadastro.showAndWait();
                }
            } else {
                Alert erroSenha = new Alert(Alert.AlertType.INFORMATION);
                erroSenha.setTitle("ERRO NO CADASTRO");
                erroSenha.setHeaderText("SoundAura");
                erroSenha.setContentText("A sua senha e confirmação de senha não coincidem. Tente novamente!");
                erroSenha.showAndWait();
            }
        } else {
            Alert erroCampos = new Alert(Alert.AlertType.INFORMATION);
            erroCampos.setTitle("ERRO NO CADASTRO");
            erroCampos.setHeaderText("SoundAura");
            erroCampos.setContentText("É necessário preencher todos os campos para realizar o cadastro.");
            erroCampos.showAndWait();
        }
    }
}
