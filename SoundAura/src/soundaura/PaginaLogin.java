package soundaura;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class PaginaLogin {

    @FXML
    private Button BotaoLogin;

    @FXML
    private Hyperlink LinkLogin3;

    @FXML
    private Pane PaneTopoTela;

    @FXML
    private TextField TextFielEmailLog;

    @FXML
    private TextField TextFieldSenhaLog;

    @FXML
    void IrParaCad(ActionEvent event) {
        Main.alterarTelas("Cadastro");
    }

    @FXML
    void RealizarLogin(ActionEvent event) {
        String email = TextFielEmailLog.getText();
        String senha = TextFieldSenhaLog.getText();

        GerenciamentoContas.login(email, senha);
    }

}


