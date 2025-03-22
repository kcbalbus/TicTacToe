import SockJS from 'sockjs-client/dist/sockjs';
import Stomp from 'webstomp-client';
import useGameStore from "@/stores/GameStore.js";
import {ROOT_URL} from "@/services/RestService.js";


class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.store = null;
    }

    startConnection() {
        return new Promise((resolve, reject) => {
            this.store = useGameStore();
            const socket = new SockJS(`${ROOT_URL}/ws?token=${this.store.userToken()}`);
            this.stompClient = Stomp.over(socket);
            this.stompClient.debug = () => {};

            const headers = {
                Authorization: `Bearer ${this.store.userToken()}`,
            };

            this.stompClient.connect(headers, () => {
                console.log('Connected with WebSocket:');
                this.subscribeToQueue();
                resolve();
            }, (error) => {
                console.error('Error while connecting with webSocket:');
                reject(error);
            });
        });
    }

    subscribeToQueue() {
        if (!this.stompClient) return;

        this.stompClient.subscribe(`/queue/${this.store.username}`, message => {
            try {
                const data = JSON.parse(message.body);
                console.log("Otrzymano wiadomość:", data);
                this.handleMessage(data);
            } catch (error) {
                console.error("Błąd przetwarzania wiadomości:", error);
            }
        });
    }

    handleMessage(data) {
        const messageType = data.dtype;

        switch (messageType) {
            case "Board":
                console.log("Otrzymano Board", data.fields);
                this.store.restoreBoard(data.fields)
                break;
            case "Room":
                console.log("Otrzymano Room", data.fields, data.roomName, data.player1, data.player2);
                this.store.restoreBoard(data.fields);
                this.store.updateRoom({roomName: data.roomName, player1: data.player1, player2: data.player2, freeSlots: data.freeSlots});
                break;
            case "GameOverMessage":
                console.log("Otrzymano GameOverMessage", data.winner, data.draw);
                this.store.setGameOver(data.winner, data.draw);
                break;
            case "OpponentLeftMessage":
                this.store.resetRoom();
                break;
            default:
                console.error("Nieznany typ wiadomości:", messageType);
        }
    }

    disconnect() {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.disconnect(() => {
                console.log('WebSocket disconnected.');
            });
            this.stompClient = null;
        }
    }


    sendMove(i, j) {
        if (!this.stompClient || !this.stompClient.connected) {
            console.log("Stomp client is not connected.");
            return;
        }

        const move = {
            roomName: this.store.room?.roomName,
            x: i,
            y: j,
            playerName: this.store.username
        };
        this.stompClient.send(`/app/move`, JSON.stringify(move));
        console.log('Sent move:', move);
    }


}

export default WebSocketService;
