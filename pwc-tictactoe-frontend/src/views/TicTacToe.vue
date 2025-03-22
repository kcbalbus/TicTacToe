<template>
  <div>
    <h2>{{gameStore.room ? `${gameStore.room.roomName.toUpperCase()}` : 'Looking for room...'}}</h2>
    <h2>{{displayPlayer()}}</h2>
    <h2>{{displayOpponent()}}</h2>
  </div>

  <div class="board">
    <div
        class="row"
        v-for="(row, rowIndex) in gameStore.board"
        :key="rowIndex"
    >
      <button
          class="btn cell"
          v-for="(cell, colIndex) in row"
          :key="colIndex"
          @click="makeMove(rowIndex, colIndex)"
          :disabled="cell !== null"
      >
        {{ cell }}
      </button>
    </div>
  </div>

  <div>
    <h2>
      {{ displayMessage()}}
    </h2>
  </div>

  <div>
      <button
          class="btn btn-primary"
          @click=router.back()>Leave room</button>
  </div>

</template>

<script setup>
import {useGameStore} from "@/stores/GameStore.js";
import {onBeforeMount, onBeforeUnmount} from "vue";
import router from "@/router/index.js";

const gameStore = useGameStore();

onBeforeMount(async () => {
  const userRoom = gameStore.getRoomDataFromSessionStorage();
  if (userRoom && !gameStore.username) {
    await gameStore.restoreRoom(userRoom);
  }
  if (gameStore.username && !gameStore.room) {
    gameStore.chooseRoomForGame();
  } else if (!gameStore.username) {
    router.push("/")
  }
})

onBeforeUnmount(async () => {
  if (gameStore.room) {
    gameStore.resetStore();
  }
})

const makeMove = (rowIndex, colIndex) => {
  console.log('Pokoik', gameStore.room)
  console.log('plansza-board', gameStore.board)

  if (gameStore.isGameOver || !gameStore.isYourTurn || gameStore.board[rowIndex][colIndex]) {
    return;
  }

  gameStore.sendMove(rowIndex, colIndex)
}

const displayPlayer = () => {
  if (gameStore.room && gameStore.room.player1 && gameStore.room.player2) {
    return `You: ${gameStore.username} (${gameStore.yourSymbol()})`;
  } else {
    return `You: ${gameStore.username}`;
  }
};

const displayOpponent = () => {
  if (gameStore.room && gameStore.room.player1 && gameStore.room.player2) {
    if (gameStore.room.player1.username === gameStore.username) {
      return `Opponent: ${gameStore.room.player2.username} (${gameStore.opponentsSymbol()})`;
    } else {
      return `Opponent: ${gameStore.room.player1.username} (${gameStore.opponentsSymbol()})`;
    }
  } else {
    return 'Looking for opponent...';
  }
};


const displayMessage = () => {
  if (gameStore.room && gameStore.room.player1 && gameStore.room.player2) {
    if (!gameStore.isGameOver){
      if(gameStore.isYourTurn){
        return 'Your turn'
      } else {
        return 'Opponents turn'
      }
    }
    else {
      if(gameStore.isWinner) {
        return 'You won ;>'
      } else if (gameStore.isWinner===false) {
        return 'You lost ;<'
      } else {
        return 'Draw ;/'
      }
    }
  } else {
    return ``;
  }
};

</script>

<style scoped>
.board {
  display: inline-block;
  margin: 20px;
}

.row {
  display: flex;
}

.cell {
  flex: 1;
  border: 1px solid black;
  text-align: center;
  font-size: 24px;
  cursor: pointer;
  margin: 0;
  width: 50px;
  height: 50px;
}
</style>