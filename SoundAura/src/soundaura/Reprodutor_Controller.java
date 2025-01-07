package soundaura;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Reprodutor_Controller {

    @FXML
    private AnchorPane AnchorReprodutor;

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


    private double volume = 30;
    private MediaPlayer mediaPlayer;
    private static Reprodutor_Controller instance;
    private FilaMusicasUnica filaDeMusicas = FilaMusicasUnica.getInstance();

    private Reprodutor_Controller() {}
    // a gnt utiliza um synchronized pois permite a utilizaçao de multithread (pois dessa forma varios processos podem ser executados ao mesmo tempo)
    public static synchronized Reprodutor_Controller getInstance() {
        if (instance == null) {
            instance = new Reprodutor_Controller();
        }
        return instance;
    }

    public void initialize() {
        ajustarVolume();
        seletorVolume.setValue(volume);
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

        if (mediaPlayer != null){
            mediaPlayer.currentTimeProperty().addListener((obs, valorAntigo, valorNovo) -> {
                if (mediaPlayer != null){
                    Duration tempo = mediaPlayer.getCurrentTime();
                    int segundosAtual = (int) tempo.toSeconds();
                    int minutos = segundosAtual / 60;
                    int segundos = segundosAtual % 60;
                    String tempoFormatado = String.format("%02d:%02d", minutos, segundos);
                    tempoAtual.setText(tempoFormatado);
        
                    if(!tempoMusica.isValueChanging()) {
                        tempoMusica.setValue(valorNovo.toSeconds() / mediaPlayer.getTotalDuration().toSeconds() * 100);
                    }
                }
            });
        } else {
            System.out.println("Ja que o media é nulo, o listener n precisa ser chamado");
        }

        mediaPlayer.setOnEndOfMedia(() -> {
            proxima();
        });
    }

    private void alterarTempoMusica(double posicao) {
        Duration duracao = mediaPlayer.getTotalDuration();
        Duration novaDuracao = duracao.multiply(posicao);

        mediaPlayer.seek(novaDuracao);
    }

    private void formartarNomeMusica(String nome) {
        nomeMusica.setText(nome);
    }

    public void reproducaoDireta(musica musica) {
        if (mediaPlayer != null){
            parar();
        }
        tocarMusica(musica);
    }

    private void alterarMusicaAtual() {
        musica musicaAtual = filaDeMusicas.getFila().get(0); // pega a primeira da fila

        Media media = new Media(new File(musicaAtual.getFilepath()).toURI().toString()); // pega caminho
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        definirTempo();
        formartarNomeMusica(musicaAtual.getNome()); // trocar o nome da musica
        mediaPlayer.play(); // toca

        filaDeMusicas.getFila().remove(0); // remove para nao gerar um looping de reprodução
    }

    public void tocarMusica(musica musica) {
        Media media = new Media(new File(musica.getFilepath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    
        mediaPlayer.setOnEndOfMedia(() -> {
            proxima();  // Quando a música terminar, chama a função que vai tocar a próxima
        });
    
        // monitorador do status de reprodução
        mediaPlayer.statusProperty().addListener((observable, StatusAnt, Status) -> {
            atualizarStatusBotao(Status); // qundo status muda a imagem do botão é atualizada
        }); 

        formartarNomeMusica(musica.getNome());  // Define o nome da música no label
        verificarMudo();  // Verifica se está no mudo
        verificarMudo(); // mantem o estado do mudo alinhado com o volume atual e com a interface do usuario
        definirTempo(); // para ir alterando o tempo da musica conforme ela passa e conforme o usuario troca a musica
        seletorVolume.setValue(volume); // voltar para o volume padrao 0.3
        atualizarStatusBotao(mediaPlayer.getStatus()); // atualizar o status do botao play pra nao ficar travado e nem desconexo com a excusão do media
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
    
    private void atualizarStatusBotao(MediaPlayer.Status status) {
        if (status == MediaPlayer.Status.PLAYING) {
            playPauseBotao.setImage(new Image(getClass().getResource("/soundaura/imagens/reproduiz.png").toString()));
        } else {
            playPauseBotao.setImage(new Image(getClass().getResource("/soundaura/imagens/pause.png").toString()));
        }
    }
    
    // botões de ação do nosso reprodutor
    @FXML
    void anterior(MouseEvent event) {
        //mediaPlayer.stop();
        //indiceMusicaAtual--;
        //if (indiceMusicaAtual < 0) {
        //    indiceMusicaAtual = listaMusicas.size() - 1;
        //}

        //alterarMusicaAtual();
        //verificarMudo();
    }

    @FXML
    public void mudo() {
        if(mediaPlayer.isMute()) {
            mediaPlayer.setMute(false);
            audio.setImage(new Image(getClass().getResource("/soundaura/imagens/volume.png").toString()));

            mediaPlayer.setVolume(volume / 100.0);
            seletorVolume.setValue(volume);
        } else {
            mediaPlayer.setMute(true);
            audio.setImage(new Image(getClass().getResource("/soundaura/imagens/silenciado.png").toString()));
            
            mediaPlayer.setVolume(0); 
            seletorVolume.setValue(0);
        }
    }

    @FXML
    public void parar() {
        mediaPlayer.stop();
    }

    @FXML
    public void playPause() {
        if (mediaPlayer == null){
            proxima();
        } else {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            verificarMudo();
            } else {
                mediaPlayer.play();
                verificarMudo();
            }
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            verificarMudo();
        } else {
            mediaPlayer.play();
            verificarMudo();
        }
    }

    @FXML
    public void proxima() {
        if (!filaDeMusicas.getFila().isEmpty()) {
            parar();
            musica proximaMusica = filaDeMusicas.getFila().get(0);  // Pega a primeira música da fila
            filaDeMusicas.getFila().remove(0);  // Remove da fila
            tocarMusica(proximaMusica);  // Toca a música
        } else {
            System.out.println("Nenhuma na fila para reproduzir.");
        }
    }

    @FXML
    public void sair(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        Stage stage = (Stage)AnchorReprodutor.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void minimizar(){
        ((Stage)AnchorReprodutor.getScene().getWindow()).toBack();
    }
}
