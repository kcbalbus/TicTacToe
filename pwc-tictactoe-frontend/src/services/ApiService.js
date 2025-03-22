import RestService, {URLS} from "./RestService";
import WebSocketService from "@/services/WebSocketService.js";

class ApiService {
    webSocket = new WebSocketService();

    async chooseRoomForPlayer(playerName) {
        try {
            return await RestService.ajax(
                `${URLS.rooms_add_player}?playerName=${playerName}`,
                "POST"
            );
        } catch (error) {
            console.error("Failed to choose room for player:", error)
        }
    }

    async deletePlayerFromRoom(roomName, playerName) {
        try {
            return await RestService.ajax(
                `${URLS.rooms_delete_player}?roomName=${roomName}&playerName=${playerName}`,
                "DELETE"
            );
        } catch (error) {
            console.error("Failed to choose room for player:", error)
        }
    }

    async getRoom(roomName) {
        try {
            return await RestService.ajax(
                `${URLS.rooms}?roomName=${roomName}`,
                "GET"
            );
        } catch (error) {
            console.error("Failed to choose room for player:", error)
        }
    }

}

export const api = new ApiService()