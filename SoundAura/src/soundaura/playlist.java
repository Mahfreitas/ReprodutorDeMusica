package soundaura;

import java.util.ArrayList;
import java.util.List;
public class playlist {

    private int idPlaylist;
    private String dataCriacao;
    private String nome;
    private Integer duracaoTotal;
    private List<musica> musicas;

    public playlist(int idPlaylist, String nome, String dataCriacao, Integer duracaoTotal) {
        this.idPlaylist = idPlaylist;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.musicas = new ArrayList<>();
        this.duracaoTotal = duracaoTotal;
    }

    public String getNome() {
        return nome;
    }
    public List<musica> getMusicas() {
        return musicas;
    }
    public String getDataCriacao() {
        return dataCriacao;
    }
    public Integer getDuracaoTotal() {
        return duracaoTotal;
    }
    public int getIdPlaylist() {
        return idPlaylist;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    //public String getDuracaoTotal() {
    //    int totalSegundos = 0;
    //    for (musica musica : musicas) {
     //       totalSegundos += stringParaSegundos(musica.getDuracao());
     //  }
      //  return segundosParaString(totalSegundos);
    //}

    //private int stringParaSegundos(String duracao) {
    //    String[] partes = duracao.split(":");
     ////   int minutos = Integer.parseInt(partes[0]);
     //   int segundos = Integer.parseInt(partes[1]);
     //   return minutos * 60 + segundos;
    //}
    //private String segundosParaString(int totalSegundos) {
      //  int minutos = totalSegundos / 60;
      //  int segundos = totalSegundos % 60;
      //  return String.format("%02d:%02d", minutos, segundos);
    //}
}
