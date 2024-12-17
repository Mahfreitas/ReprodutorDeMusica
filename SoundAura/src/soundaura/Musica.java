package soundaura;

public class Musica {
    private String nome;
    private String artista;
    private String genero;
    private String duracao;
    private String dataAdicionada;

    public Musica(String nome, String artista, String genero, String duracao, String dataAdicionada) {
        this.nome = nome;
        this.artista = artista;
        this.genero = genero;
        this.duracao = duracao;
        this.dataAdicionada = dataAdicionada;
    }

    public String getNome() {
        return nome;
    }

    public String getArtista() {
        return artista;
    }

    public String getGenero() {
        return genero;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getDataAdicionada() {
        return dataAdicionada;
    }
}
