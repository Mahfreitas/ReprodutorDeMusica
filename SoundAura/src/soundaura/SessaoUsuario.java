package soundaura;
public class SessaoUsuario {
    private static SessaoUsuario instancia;
    private int idUsuario;
    private playlist playlistAtual;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        System.out.println("Setando ID do usuário: " + idUsuario);
        this.idUsuario = idUsuario;
    }

    public void encerrarSessao() {
        System.out.println("Encerrando sessão.");
        idUsuario = 0;
        instancia = null;
    }

    public playlist getPlaylistAtual() {
        return playlistAtual;
    }

    public void setPlaylistAtual(playlist playlistAtual) {
        this.playlistAtual = playlistAtual;
    }
}