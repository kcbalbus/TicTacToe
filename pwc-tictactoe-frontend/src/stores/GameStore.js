import {defineStore} from 'pinia';
import {api} from "@/services/ApiService.js";
import awsSecrets from "@/aws/aws-exports.js";

export const useGameStore = defineStore({
    id: 'game',
    state: () => ({
        username: "",
        room: undefined,
        board: Array.from({ length: 3 }, () => Array(3).fill(null)),
        gameInProgress: false,
        isYourTurn: false,
        isGameOver: false,
        isWinner: undefined
    }),
    getters: {
        gameGame: state => state.game,
    },
    actions: {
        startWebSocketConnection(){
            api.webSocket.startConnection();
        },
        setUsername(username){
            this.username = username;
            this.resetBoard();
        },
        async chooseRoomForGame(){
          try{
              await this.startWebSocketConnection();
              await new Promise(resolve => setTimeout(resolve, 1000));

              this.room = await api.chooseRoomForPlayer(this.username);
              this.saveRoomDataToSessionStorage();
              return true;
          }   catch (error) {
              console.error('Błąd, ', error)
              return error;
          }
        },
        saveRoomDataToSessionStorage() {
            const dataToStore = {
                username: this.username,
                room: this.room
            }
            sessionStorage.setItem('roomData', JSON.stringify(dataToStore));
        },
        getRoomDataFromSessionStorage() {
            const roomData = sessionStorage.getItem('roomData');
            if (roomData) {
                return JSON.parse(roomData);
            } else {
                return null;
            }
        },
        restoreBoard(board) {
            console.log('plansza-serwer', board)
            this.updateEntireBoard(board);
            this.startGame();
        },
        updateEntireBoard(board) {
            for (let i = 0; i < 3; i++) {
                for (let j = 0; j < 3; j++) {
                    this.board[i][j] = board[i][j] === 1 ? "X" : board[i][j] === 2 ? "O" : null;
                }
            }
            console.log('plansza-klient', this.board)
        },
        startGame() {
            this.gameInProgress = true;
            this.isYourTurn = true;
        },
        updateRoom(room) {
            this.room = room;
            this.updatePlayerTurnFromRoom();
        },
        updatePlayerTurnFromRoom() {
            if (this.username === this.room?.player1.username) {
                this.isYourTurn = this.room.player1.starting;
            } else if (this.username === this.room?.player2.username) {
                this.isYourTurn = this.room.player2.starting;
            } else {
                this.isYourTurn = false;
            }
        },
        resetBoard() {
            this.board = Array.from({ length: 3 }, () => Array(3).fill(null));
        },
        async restoreRoom(userRoom) {
            if (userRoom && !this.username) {
                this.username = userRoom.username;
                try {
                    this.room = await api.getRoom(userRoom.room.roomName);
                    this.startWebSocketConnection();
                    this.restoreBoard(this.room.fields);

                    this.updatePlayerTurnFromRoom();
                    console.log('is your turn?', this.isYourTurn);
                } catch (error) {
                    console.error("Nie udało się przywrócić pokoju:", error);
                }
            }
        },
        setGameOver(winner, draw) {
            this.isGameOver = true;
            this.gameInProgress = false;
            this.isWinner = draw ? null : winner;
        },
        yourSymbol() {
            if (this.username === this.room?.player1.username) {
                return 'X';
            } else if (this.username === this.room?.player2.username) {
                return 'O';
            } else {
                return null;
            }
        },
        opponentsSymbol() {
            if (this.username === this.room?.player1.username) {
                return 'O';
            } else if (this.username === this.room?.player2.username) {
                return 'X';
            } else {
                return null;
            }
        },
        sendMove(i, j) {
            this.isYourTurn = false;
            this.board[i][j] = this.yourSymbol();
            api.webSocket.sendMove(i, j);
        },
        deletePlayerFromRoom(roomName, username) {
            try {
                api.deletePlayerFromRoom(roomName, username);
                this.resetStore()
                return true;
            } catch (error) {
                return error;
            }
        },
        resetStore() {
            this.username = '';
            this.resetRoom();
            api.webSocket.disconnect();
        },
        resetRoom() {
            this.gameInProgress = false;
            this.room = null;
            this.isYourTurn = false;
            this.isGameOver = false;
            this.isWinner = null;
            this.resetBoard();
        },
        userToken(){
            return localStorage.getItem(`CognitoIdentityServiceProvider.${awsSecrets.aws_user_pools_web_client_id}.${this.username}.accessToken`);
        }
    }
});

export default useGameStore;