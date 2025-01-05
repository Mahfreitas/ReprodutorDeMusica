package soundaura;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class Reprodutor_Controller {

    @FXML
    private ImageView audio;

    @FXML
    private ImageView playPauseBotao;

    @FXML
    private MediaView mediaView;

    @FXML
    private Label nomeMusica;

    @FXML
    private Slider seletorVolume;

    @FXML
    private Label tempoAtual;

    @FXML
    private Slider tempoMusica;

    @FXML
    private Label tempoTotal;

    private MediaPlayer mediaPlayer;
    private List<String> musicas;
    private int indiceMusicaAtual;
    private double volume = 30;

    public void initialize() {
        seletorVolume.setValue(volume);
        carregarMusica();
        formartarNomeMusica();
        definirTempo();
        ajustarVolume();
    }

    private void definirTempo() {
        mediaPlayer.setOnReady(() -> {
            Duration tempoTotalMusica = mediaPlayer.getTotalDuration();
            int totalSegundos = (int) tempoTotalMusica.toSeconds();
            int minutos = totalSegundos / 60;
            int segundos = totalSegundos % 60;

            String tempoFormatado = String.format("%02d:%02d", minutos, segundos);
            tempoTotal.setText(tempoFormatado);
        });

        tempoMusica.valueProperty().addListener((obs, valorAntigo, valorNovo) -> {
            if (tempoMusica.isValueChanging()) {
                double posicao = valorNovo.doubleValue() / 100;
                alterarTempoMusica(posicao);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((obs, valorAntigo, valorNovo) -> {
            Duration tempo = mediaPlayer.getCurrentTime();
            int segundosAtual = (int) tempo.toSeconds();
            int minutos = segundosAtual / 60;
            int segundos = segundosAtual % 60;
            String tempoFormatado = String.format("%02d:%02d", minutos, segundos);
            tempoAtual.setText(tempoFormatado);

            if(!tempoMusica.isValueChanging()) {
                tempoMusica.setValue(valorNovo.toSeconds() / mediaPlayer.getTotalDuration().toSeconds() * 100);
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            proxima();
        });
    }

    private void alterarTempoMusica(double posicao) {
        Duration duracao = mediaPlayer.getTotalDuration();
        Duration novaDuracao = duracao.multiply(posicao);

        mediaPlayer.seek(novaDuracao);
    }

    private void formartarNomeMusica() {
        String nome = musicas.get(indiceMusicaAtual);
        nomeMusica.setText(nome.replace("src\\reprodutor\\music\\", ""));
    }

    public void carregarMusica() {
        musicas = new ArrayList<>();
        musicas.add("C:\\Users\\Guthora\\MinhasMusicasSoundAura\\Usuario_1\\13.mp3");
        musicas.add("C:\\Users\\Guthora\\MinhasMusicasSoundAura\\Usuario_1\\14.mp3");
        indiceMusicaAtual = 0;
        String musicaAtual = musicas.get(indiceMusicaAtual);

        Media media = new Media(new File(musicaAtual).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setVolume(volume);
    }

    private void alterarMusicaAtual() {
        String musicaAtual = musicas.get(indiceMusicaAtual);

        Media media = new Media(new File(musicaAtual).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        definirTempo();
        formartarNomeMusica();
        mediaPlayer.play();
    }

    private void ajustarVolume() {

        seletorVolume.valueProperty().addListener((obs, valorAntigo, valorNovo) -> {
            double volume = valorNovo.doubleValue() / 100;
            mediaPlayer.setVolume(volume);

            if (volume > 0 && mediaPlayer.isMute()) {
                mudo();
            }

            if(this.volume!=0){
                mediaPlayer.setMute(false);
            }

            audio.setImage(volume > 0 ? new Image(getClass().getResource("/soundaura/imagens/volume.png").toString()) : new Image(getClass().getResource("/soundaura/imagens/silenciado.png").toString()));
        });
    }

    private void verificarMudo(){
        if(volume == 0){
            mediaPlayer.setMute(true);
            mediaPlayer.setVolume(0);
        }
    }
    
    @FXML
    void anterior(MouseEvent event) {
        mediaPlayer.stop();
        indiceMusicaAtual--;
        if (indiceMusicaAtual < 0) {
            indiceMusicaAtual = musicas.size() - 1;
        }

        alterarMusicaAtual();
        verificarMudo();
    }

    @FXML
    public void mudo() {
        if(mediaPlayer.isMute()) {
            mediaPlayer.setMute(false);
            audio.setImage(new Image(getClass().getResource("/soundaura/imagens/volume.png").toString()));
            mediaPlayer.setVolume(seletorVolume.getValue());
            volume = seletorVolume.getValue();
        } else {
            mediaPlayer.setMute(true);
            audio.setImage(new Image(getClass().getResource("/soundaura/imagens/silenciado.png").toString()));
            volume = 0;
        }
    }

    @FXML
    public void parar() {
        mediaPlayer.stop();
        playPause();
    }

    @FXML
    public void playPause() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            verificarMudo();
            playPauseBotao.setImage(new Image(getClass().getResource("/soundaura/imagens/pause.png").toString()));
        } else {
            mediaPlayer.play();
            verificarMudo();
            playPauseBotao.setImage(new Image(getClass().getResource("/soundaura/imagens/reproduiz.png").toString()));
        }
    }

    @FXML
    public void proxima() {
        mediaPlayer.stop();
        indiceMusicaAtual++;
        if (indiceMusicaAtual >= musicas.size()) {
            indiceMusicaAtual = 0;
        }
        alterarMusicaAtual();
        verificarMudo();
    }

    @FXML
    public void sair(){

    }

    @FXML
    public void minimizar(){
        
    }
}
