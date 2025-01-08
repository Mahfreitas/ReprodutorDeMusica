package SoundAura;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ContaController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField senhaField;

    @FXML
    private Button btnTrocarEmail;

    @FXML
    private Button btnTrocarSenha;

    @FXML
    public void initialize() {
        btnTrocarEmail.setOnAction(event -> trocarEmail());
        btnTrocarSenha.setOnAction(event -> trocarSenha());
    }

    private void trocarEmail() {
        String novoEmail = emailField.getText();

        if (novoEmail.isEmpty() || !novoEmail.matches("^\\S+@\\S+\\.\\S+$")) {
            mostrarAlerta("Erro", "E-mail inválido.", Alert.AlertType.ERROR);
            return;
        }

        if (ContaDAO.atualizarEmail(novoEmail)) {
            mostrarAlerta("Sucesso", "E-mail atualizado com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Erro", "Erro ao atualizar o e-mail. Tente novamente.", Alert.AlertType.ERROR);
        }
    }

    private void trocarSenha() {
        String novaSenha = senhaField.getText();

        if (novaSenha.isEmpty() || novaSenha.length() < 8) {
            mostrarAlerta("Erro", "A senha deve ter pelo menos 8 caracteres.", Alert.AlertType.ERROR);
            return;
        }

        if (ContaDAO.atualizarSenha(novaSenha)) {
            mostrarAlerta("Sucesso", "Senha atualizada com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Erro", "Erro ao atualizar a senha. Tente novamente.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
