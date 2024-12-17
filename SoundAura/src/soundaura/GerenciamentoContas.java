package soundaura;

import java.io.File;
import java.sql.*;
import javafx.scene.control.Alert;

public class GerenciamentoContas {
    // Register user in the database

    private static Connection connectToDatabase() throws SQLException {
            String url = MySQL.getUrl();
            String user = MySQL.getUser();
            String password = MySQL.getPassword();
        
            return DriverManager.getConnection(url, user, password);
        }
    
        public static void registrar(String email, String senha, String confSenha) {
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

                                String homeDir = System.getProperty("user.home");
                                File novaPasta = new File(homeDir + File.separator + "MinhasMusicasSoundAura");
                                if (!novaPasta.exists()) {
                                    novaPasta.mkdir();
                                }

                                Main.alterarTelas("Login");
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

    public static boolean login(String email, String password) {
        try (Connection conn = connectToDatabase()) {
            String loginQuery = "SELECT id_usuario FROM Users WHERE email_usuario = ? AND senha_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(loginQuery)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    int idUsuario = resultSet.getInt("id_usuario");
                    SessaoUsuario.getInstancia().setIdUsuario(idUsuario);
                    Main.alterarTelas("PaginaPrimaria");
                    return true;
                } else {
                    Alert erroLogin = new Alert(Alert.AlertType.INFORMATION);
                    erroLogin.setTitle("ERRO NO LOGIN");
                    erroLogin.setHeaderText("SoundAura");
                    erroLogin.setContentText("Senha ou Email estão incorretos, tente novamente.");
                    erroLogin.showAndWait();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert erroLogin = new Alert(Alert.AlertType.ERROR);
            erroLogin.setTitle("ERRO NO LOGIN");
            erroLogin.setHeaderText("SoundAura");
            erroLogin.setContentText("Ocorreu um erro ao tentar realizar o login.");
            erroLogin.showAndWait();
            return false;
        }
    }
}
