package soundaura;

public class Conta {
    private String email;
    private String senha;
    private String confSenha;

    public Conta(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getemail() {
        return email;
    }

    public String getPassword() {
        return senha;
    }
    public String getConfSenha() {
        return confSenha;
    }
}
