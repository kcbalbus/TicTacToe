import axios from "axios";
import router from "@/router";
import gameStore, {useGameStore} from "@/stores/GameStore.js";

export const ROOT_URL = 'http://pwcappenvironment.eba-5sxjmuy9.us-east-1.elasticbeanstalk.com:8080';
export const URLS = {
    rooms: `${ROOT_URL}/rooms`,
    rooms_add_player: `${ROOT_URL}/rooms/findRoomForPlayer`,
    rooms_delete_player: `${ROOT_URL}/rooms/deletePlayerFromRoom`

};

export const AUTH_HEADER = () => {
    const gameStore = useGameStore()
    let token = gameStore.userToken()
        return {
            Authorization: `Bearer ${token}`,
        };

};

class RestService {
    static async ajax(url, method, headers) {
        const config = {
            url,
            method,
            headers: {...headers, ...AUTH_HEADER()},
        };

        return await axios.request(config).then((response) => {
            return response.data;
        });

    }

}

export default RestService;
