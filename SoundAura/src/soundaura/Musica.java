package soundaura;

public class Musica {
    private String nome;
    private String artista;
    private String album;
    private String duracao;
    private String dataAdicionada;
    private String genero;
    private String filepath;

    public Musica(String nome, String artista, String album, String duracao, String dataAdicionada, String genero, String filepath) {
        this.nome = nome;
        this.artista = artista;
        this.album = album;
        this.duracao = duracao;
        this.dataAdicionada = dataAdicionada;
        this.genero = genero;
        this.filepath = filepath;
    }

    public String getNome() {
        return nome;
    }

    public String getAlbum() {
        return album;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getGenero() {
        return genero;
    }

    public String getArtista() {
        return artista;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getDataAdicionada() {
        return dataAdicionada;
    }
}
