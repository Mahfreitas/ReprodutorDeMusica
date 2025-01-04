package soundaura;

public class conta {
    private String email;
    private String senha;
    private String confSenha;

    public conta(String email, String senha) {
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
