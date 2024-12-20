-- Create the database
CREATE DATABASE IF NOT EXISTS SoundAuraDB;
USE SoundAuraDB;

-- Tabela: Users
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Email VARCHAR(255) NOT NULL UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: Playlists
CREATE TABLE Playlists (
    PlaylistID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Name VARCHAR(255) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- Tabela: Songs
CREATE TABLE Songs (
    SongID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Artist VARCHAR(255),
    Album VARCHAR(255),
    FilePath VARCHAR(500) NOT NULL UNIQUE,
    Duration INT, -- temp em segundos
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: PlaylistSongs
CREATE TABLE PlaylistSongs (
    PlaylistSongID INT AUTO_INCREMENT PRIMARY KEY,
    PlaylistID INT NOT NULL,
    SongID INT NOT NULL,
    AddedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PlaylistID) REFERENCES Playlists(PlaylistID) ON DELETE CASCADE,
    FOREIGN KEY (SongID) REFERENCES Songs(SongID) ON DELETE CASCADE
);

-- Tabela: FavoriteSongs
CREATE TABLE FavoriteSongs (
    FavoriteID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    SongID INT NOT NULL,
    MarkedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (SongID) REFERENCES Songs(SongID) ON DELETE CASCADE
);
