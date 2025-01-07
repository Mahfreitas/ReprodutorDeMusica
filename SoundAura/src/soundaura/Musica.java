package soundaura;

import java.io.File;

public class musica {
    private String nome;
    private String artista;
    private String album;
    private String duracao;
    private String dataAdicionada;
    private String genero;
    private String filepath;
    private Integer id;
    private boolean favorita;
    private boolean arquivoDisponivel;

    public musica(String nome, String artista, String album, String duracao, String dataAdicionada, String genero, String filepath, Integer id) {
        this.nome = nome;
        this.artista = artista;
        this.album = album;
        this.duracao = duracao;
        this.dataAdicionada = dataAdicionada;
        this.genero = genero;
        this.filepath = filepath;
        this.id = id;
        this.arquivoDisponivel = new File(filepath).exists(); // verifica de o arquivo existe de fato no dispositivo conectado a conta
    }

    public String getNome() {
        return nome;
    }

    public boolean arquivoDisponivel(){
        return arquivoDisponivel;
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

    public Integer getId() {
        return id;
    }

    public boolean getFavorita(){
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }
    public boolean isFavorita() {
        return favorita;
    }
}
