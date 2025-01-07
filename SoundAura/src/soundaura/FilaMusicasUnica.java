package soundaura;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FilaMusicasUnica {
    private static FilaMusicasUnica instancia;
    
    private ObservableList<musica> filaDeMusicas = FXCollections.observableArrayList();

    private FilaMusicasUnica() {
    }

    public static FilaMusicasUnica getInstance() {
        if (instancia == null) {
            instancia = new FilaMusicasUnica();
        }
        return instancia;
    }

    public void limparFila(){
        filaDeMusicas.clear();
    }

    // aqui é basicamente pra gnt pegar fila basicamente
    public ObservableList<musica> getFila() {
        return filaDeMusicas;
    }

    public void adicionarMusica(musica musica) {
        filaDeMusicas.add(musica);
        System.out.println("Musica adicionada :" + musica);
    }

    public void removerMusica(musica musica) {
        filaDeMusicas.remove(musica);
        System.out.println("Musica removida :" + musica);
    }
}
