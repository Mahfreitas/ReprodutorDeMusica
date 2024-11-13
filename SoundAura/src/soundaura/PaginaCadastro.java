package soundaura;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class PaginaCadastro {

    @FXML
    private Button BotaoCadastro;

    @FXML
    private Hyperlink LinkLogin1;

    @FXML
    private Hyperlink LinkLogin2;

    @FXML
    private Pane PaneTopoTela;

    @FXML
    private TextField TextFielEmailCad;

    @FXML
    private TextField TextFieldConfSenhaCad;

    @FXML
    private TextField TextFieldSenhaCad;

    @FXML
    void IrParaLogin(ActionEvent event) {
        Main.alterarTelas("Login");
    }

    @FXML
    void RealizarCadastro(ActionEvent event) {
        String email = TextFielEmailCad.getText();
        String senha = TextFieldSenhaCad.getText();
        String confSenha = TextFieldConfSenhaCad.getText();

        GerenciamentoContas.registrar(email, senha, confSenha);
    }

}

