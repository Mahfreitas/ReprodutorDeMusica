package soundaura;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;

public class GerenciamentoContas {
    private static List<Conta> Usuarios = new ArrayList<>();;


    public static void registrar(String email, String senha, String confSenha) {
        for (Conta Usuario : Usuarios) {
            if (Usuario.getemail().equals(email)) {
                Alert CadastroRealizado = new Alert(Alert.AlertType.INFORMATION);
                CadastroRealizado.setTitle("ERRO NO CADASTRO");
                CadastroRealizado.setHeaderText("SoundAura");
                CadastroRealizado.setContentText("O usuario em questão já existe, tente novamente");
                CadastroRealizado.showAndWait();
                break;
            }
        }

        if(!email.isEmpty() && !senha.isEmpty() && !confSenha.isEmpty()){
            //CONFIRMAção de senha
            if(senha.equals(confSenha)){
                Usuarios.add(new Conta(email, senha));
                Alert CadastroRealizado = new Alert(Alert.AlertType.INFORMATION);
                CadastroRealizado.setTitle("Cadastro Realizado com Sucesso");
                CadastroRealizado.setHeaderText("SoundAura");
                CadastroRealizado.setContentText("O seu cadastro foi realizado com sucesso");
                CadastroRealizado.showAndWait();
                Main.alterarTelas("PaginaPrimaria");
            } else {
                Alert ErroCadastro1 = new Alert(Alert.AlertType.INFORMATION);
                ErroCadastro1.setTitle("ERRO NO CADASTRO");
                ErroCadastro1.setHeaderText("SoundAura");
                ErroCadastro1.setContentText("A sua os campos de 'senha' e 'confirmação de senha' estão diferentes, tente novamente!");
                ErroCadastro1.showAndWait();
            }
        } else {
            Alert ErroCadastro1 = new Alert(Alert.AlertType.INFORMATION);
            ErroCadastro1.setTitle("ERRO NO CADASTRO");
            ErroCadastro1.setHeaderText("SoundAura");
            ErroCadastro1.setContentText("É necessario preencher todas as informações para que o cadastro seja realizado");
            ErroCadastro1.showAndWait();
        }
    }

    public static boolean login(String email, String password) {
        for (Conta Usuario : Usuarios) {
            if (Usuario.getemail().equals(email) && Usuario.getPassword().equals(password)) {
                Main.alterarTelas("Primaria");
                return true;
            }
        }
        Alert LoginRealizado = new Alert(Alert.AlertType.INFORMATION);
        LoginRealizado.setTitle("ERRO NO LOGIN");
        LoginRealizado.setHeaderText("SoundAura");
        LoginRealizado.setContentText("Senha ou Email estão incorretos, tente novemente.");
        LoginRealizado.showAndWait();
        return false;
    }
}
