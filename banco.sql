create database sound_aura;
use sound_aura;

create table usuario(
	id_usuario int auto_increment primary key,
    nome_usuario varchar(100) not null,
    email_usuario varchar(100) not null,
    reproducaoH_usuario varchar(200)
);
create table musica (
	id_musica int auto_increment primary key,
    duracao_musica int not null,
    nome_musica varchar(100) not null,
    filepath_musica varchar(500) not null unique,
    horario_addMS timestamp default current_timestamp,
	artistas_musica varchar(100),
    album_musica varchar(100),
    favorita_musica boolean,
    genero_musica varchar(100)
);

create table playlist (
	id_playlist int auto_increment primary key,
    id_usuario int not null,
    nome_playlist varchar(255) not null,
    horario_addPS timestamp default current_timestamp,
    duracao_total int,
	FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) on delete cascade
);

create table Mplaylist (
	Mplaylist_id int auto_increment primary key,
    playlist_id int not null,
    musica_id int not null,
    horario_addP timestamp default current_timestamp,
	FOREIGN KEY (playlist_id) REFERENCES playlist(id_playlist) ON DELETE CASCADE,
    FOREIGN KEY (musica_id) REFERENCES musica(id_musica) ON DELETE CASCADE
);

CREATE TABLE Fmusica (
    FavoriteID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    SongID INT NOT NULL,
    MarkedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (SongID) REFERENCES Songs(SongID) ON DELETE CASCADE
);
