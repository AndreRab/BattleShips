# Battleship Game

## Introduction

The Battleship Game application is designed for playing the Battleship game over a network. This project aims to create an engaging and interactive multiplayer experience where players can strategize and compete against each other in the classic game of Battleship.

## Features

- **Networked Gameplay:** The application connects with another instance of the application over a network, allowing players to engage in real-time Battleship matches.

- **Game Parameters:** The application supports various command-line parameters to specify the game mode (`server` or `client`), server address (`-server`), communication port (`-port`), and map file path (`-map`).

- **Map Generation:** Maps for the game are generated using the same format as described in assignment number 3 from the collections section. The application utilizes a random map generator to create diverse and challenging game environments.

- **Communication Protocol:** Gameplay communication occurs over TCP protocol with UTF-8 encoding. Players exchange messages consisting of commands and coordinates, separated by a semicolon (`;`) and terminated by a newline character (`\n`).

- **Game Commands:** Players send commands such as `start`, `pudło`, `trafiony`, `trafiony zatopiony`, and `ostatni zatopiony` to indicate game events like starting the game, hitting an opponent's ship, sinking a ship, and ending the game.

- **Error Handling:** The application handles errors gracefully, reattempting communication upon receiving an unclear command or waiting for one second. After three unsuccessful attempts, the application displays an error message and terminates.

## Usage

The Battleship Game application provides a command-line interface for configuring and playing the game. Below are some of the supported command-line parameters:

- `-mode [server|client]`: Specifies the operating mode of the application (`server` or `client`).
- `-server S`: Specifies the server address to connect to (only applicable in client mode).
- `-port N`: Specifies the communication port for the application.
- `-map map-file`: Specifies the path to the file containing the map layout for ship placement.

## Conclusion

The Battleship Game application offers an immersive multiplayer experience where players can engage in strategic naval combat over a network. With its intuitive command-line interface and robust networking capabilities, the application provides hours of entertainment for players of all skill levels. Challenge your friends to a thrilling game of Battleship and see who will emerge victorious!
