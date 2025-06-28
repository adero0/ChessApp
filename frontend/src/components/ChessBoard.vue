<template>
  <div class="app-root">
    <div class="chess-container">
      <div class="chess-board" :class="{'flipped': playerColor === 'BLACK' }">
        <div class="turn-message-container" :class="{'rotatore': playerColor === 'BLACK'}">
          <div class="bg-green-600 bg-opacity-70 text-white p-4 rounded max-w-xs mx-auto"
               v-show="isMyTurn">
            Twoja kolej!
          </div>
        </div>
        <div v-for="(row, rowIndex) in board" :key="rowIndex" class="row">
          <div
              v-for="(square, colIndex) in row"
              :key="colIndex"
              class="square"
              :class="[
                (rowIndex + colIndex) % 2 === 0 ? 'light' : 'dark',
                { 'legal-move': highlightedSquares.includes(square.square) },
                { 'check-square': checkSquare !== -1 && square.square === checkSquare },
                { 'last-move': square.square === lastMove.from || square.square === lastMove.to },
                { 'game-over': checkmate },
              ]"
              @dragover.prevent="!gameOver"
              @drop="!gameOver ? handleDrop($event, square.square) : null"
          >
            <img
                v-if="!square.isEmpty"
                :src="pieceImages[square.symbol]"
                :alt="square.symbol"
                class="piece"
                :class="{'opacity-70': gameOver}"
                @click="!gameOver ? showLegalMoves(square.square) : null"
                @dragstart="!gameOver ? handleDragStart(square, $event) : null"
                @dragend="checkIsLegal($event)"
            >
          </div>
        </div>
      </div>

      <div class="right-panel">
        <button
            v-if="!gameOver"
            @click="confirmResignation"
            class="resign-button bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded shadow hover:shadow-md transition duration-200"
        >
          Poddaj partię
        </button>
        <div class="pgn-container">
          <button
              class="pgn-button"
              @click="copyPgnToClipboard"
              :class="{ 'scrollable-pgn': pgnLines > 10 }"
          >
            {{ pgn }}
          </button>
        </div>
      </div>
    </div>

    <!-- Game Over Modal - Now outside chess-container -->
    <div v-if="checkmate" class="game-over-overlay" @click.self="checkmate = false">
      <div class="game-over-modal">
        <div class="game-over-header">
          <span class="game-over-title">GAME OVER</span>
          <button class="close-button" @click="checkmate = false">&times;</button>
        </div>
        <div v-if="resignation" class="resignation-message">
          Partia zakończona przez poddanie
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
// Import all piece SVGs
import blackRook from '../assets/piecesSVG/black-rook.svg';
import blackPawn from '../assets/piecesSVG/black-pawn.svg';
import blackKnight from '../assets/piecesSVG/black-knight.svg';
import blackBishop from '../assets/piecesSVG/black-bishop.svg';
import blackQueen from '../assets/piecesSVG/black-queen.svg';
import blackKing from '../assets/piecesSVG/black-king.svg';

import whiteRook from '../assets/piecesSVG/white-rook.svg';
import whitePawn from '../assets/piecesSVG/white-pawn.svg';
import whiteKnight from '../assets/piecesSVG/white-knight.svg';
import whiteBishop from '../assets/piecesSVG/white-bishop.svg';
import whiteQueen from '../assets/piecesSVG/white-queen.svg';
import whiteKing from '../assets/piecesSVG/white-king.svg';
import {useAuthStore} from "../stores/auth.js";
import {useRouter} from "vue-router";


const CastlingType = {
  SHORTCASTLE: 'SHORTCASTLE',
  LONGCASTLE: 'LONGCASTLE',
  NOCASTLE: 'NOCASTLE'
};

export default {
  mounted() {
    this.gameId = this.$route.query.gameId;
    this.isBotGame = this.$route.path.includes('/bot_game');
    if (this.isBotGame) {
      this.difficulty = this.$route.query.difficulty;
      this.getLegalsOrMakeBotMove();
    } else if (this.gameId) {
      this.startGamePolling();
    } else {
      console.error("this is pretty bad")
    }
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return {authStore, router};
  },
  methods: {
    confirmResignation() {
      if (confirm("Czy napewno poddać partię?")) {
        this.resignGame();
      }
    },
    resignGame() {
      try {
        this.checkmate = true;
        this.gameOver = true;
        this.resignation = true;
        axios.post(`http://localhost:8080/api/game/${this.gameId}/resign`, {}, {
          headers: {
            Authorization: `Bearer ${this.authStore.token}`
          }
        }).then(() => {
        });
      } catch (error) {
        console.log("error in resign", error)
      }
    },
    startGamePolling() {
      this.pollInterval = setInterval(async () => {
        try {
          const response = await axios.get(
              `http://localhost:8080/api/game/${this.gameId}/status`,
              {
                headers: {
                  Authorization: `Bearer ${this.authStore.token}`
                }
              });
          const status = response.data;

          if (status.lastMove) {
            this.serverFen = status.fen;
            this.localFen = status.fen;
            this.lastMove = {
              from: status.lastMove.from,
              to: status.lastMove.to
            };
          }

          if (!this.serverFen) {
            this.serverFen = status.fen;
            this.localFen = status.fen;
          } else {
            this.serverFen = status.fen;
          }


          this.playerColor = status.playerColor;
          this.isMyTurn = status.currentTurn === this.playerColor;

          if (!this.isMyTurn) {
            this.localFen = this.serverFen; // Sync to server state when not our turn
          }
          // this.pgn = status.pgn; //tutaj
          this.pgn += status.pgn.substring(status.pgn.indexOf(this.pgn) + this.pgn.length);

          if (status.isCheckmate) {
            this.checkmate = status.isCheckmate;
            this.gameOver = true;
            this.pgn += '#';
            clearInterval(this.pollInterval);
          }


          this.checkSquare = status.checkSquare;
          this.materialCount = status.materialCount;
          this.currentTurn = status.currentTurn;
          this.opponentUsername = status.opponentUsername.username;
          this.opponentId = status.opponentId;

          if (status.legalMoves) {
            this.legalMoves = status.legalMoves;
          }

        } catch (error) {
          console.error('Error polling game status:', error);
        }
      }, 75); // Poll every 2 seconds
    },
    openSuccessCopyToast() {
      this.$toast.open({
        message: "Copied PGN to clipboard",
        type: "info",
        duration: 1000 * 10,
        dismissible: true,
      })
    },
    async copyPgnToClipboard() {
      try {
        await navigator.clipboard.writeText(this.pgn);
        this.openSuccessCopyToast()
      } catch ($e) {
        alert('Cannot copy');
      }
    },
    chessPositionIntegerToSquare(square) {
      if (!(square >= 0 && square <= 63)) {
        throw new Error("Incorrect square value")
      }
      let square_name = ''
      square_name += String.fromCharCode(((square ^ 7) % 8) + 97);
      square_name += Math.floor(square / 8 + 1);
      return square_name;
    },
    chessPositionSquareToInteger(square_character) {
      let number = 104 - square_character.charCodeAt(0);
      number += (square_character.charAt(1) - 1) * 8;
      return number;
    },

    beforeUnmount() {
      clearInterval(this.pollInterval);
    },

    async getLegalsOrMakeBotMove() {
      try {
        const response = await axios.get(`http://localhost:8080/api/game/bot_game/${this.gameId}/setup`,
            {
              headers: {
                Authorization: `Bearer ${this.authStore.token}`
              }
            }
        );
        console.log(response.data);
        this.playerColor = response.data.playerColor;
        this.fen = response.data.fen;
        console.log(response.data.fen);
        this.localFen = this.fen;// Initialize localFen
        this.serverFen = this.fen; // Initialize serverFen
        const fenParts = this.fen.split(' ');
        this.currentTurn = fenParts[1] === 'w' ? 'WHITE' : 'BLACK';
        this.isMyTurn = this.currentTurn === this.playerColor;
        // Parse currentTurn from FEN
        if (this.playerColor !== this.currentTurn) {
          await this.getStockfishMoves();
        } else {
          this.legalMoves = response.data.legalMoves;
        }
      } catch (error) {
        console.error('Error in bot setup or smth:', error);
      }
    },
    async getStockfishMoves() {
      try {
        console.log("Wysyłam botowi taki fen: ", this.serverFen);
        const botMove = await axios.get('https://stockfish.online/api/s/v2.php', {
          params: {fen: this.serverFen, depth: this.difficulty}
        });
        console.log(botMove.data);
        const move = botMove.data.bestmove.substring(9, 13);
        const from = this.chessPositionSquareToInteger(move.substring(0, 2));
        const to = this.chessPositionSquareToInteger(move.substring(2, 4));
        console.log(from, " ", to);
        const response = await axios.post(`http://localhost:8080/api/game/bot_game/bot_move/${this.gameId}/move`, {
          from: from,
          to: to,
          piece: this.getPieceSymbol(from),
          castlingType: CastlingType.NOCASTLE
        }, {headers: {Authorization: `Bearer ${this.authStore.token}`}});
        console.log(response.data);
        this.serverFen = response.data.fen;
        this.localFen = response.data.fen;
        this.fen = response.data.fen;
        this.turnCounter++;
        this.pgn = response.data.pgn; // tutaj

        this.checkmate = response.data.checkmate;
        this.gameOver = this.checkmate;
        this.lastMove = {from: from, to: to};
        this.checkSquare = response.data.enemyKingInCheck;
        this.legalMoves = response.data.legalMoves;
        this.castlingRights = response.data.castlingRights;
        this.enPassantSquare = response.data.enPassantSquare;
        this.highlightedSquares = [];
        this.materialCount = response.data.materialImbalance;
        this.currentTurn = (this.currentTurn === "WHITE") ? "BLACK" : "WHITE";
        this.isMyTurn = true;
      } catch (error) {
        console.error('Stockfish move failed:', error);
      }
    },

    getPieceSymbol(square) {
      const pieceData = this.board.flat().find(s => s.square === square);
      return pieceData ? pieceData.symbol : '';
    },

    handleDragStart(squareData, event) {
      if (this.checkmate || this.gameOver) {
        event.preventDefault();
        return;
      }
      this.highlightedSquares = this.legalMoves[squareData.square] || [];
      const currentTurn = this.fen.split(' ')[1];
      const pieceColor = squareData.symbol === squareData.symbol.toLowerCase() ? 'black' : 'white';

      if ((currentTurn === 'w' && pieceColor !== 'white') ||
          (currentTurn === 'b' && pieceColor !== 'black')) {
        event.preventDefault();
        return;
      }

      const dragInfo = {
        from: squareData.square,
        piece: squareData.symbol,
        originalFEN: this.fen,
        castlingType: CastlingType.NOCASTLE
      };

      this.legalDrags = this.legalMoves[squareData.square] || [];
      event.dataTransfer.setData('text/plain', JSON.stringify(dragInfo));
    },

    async handleDrop(event, targetSquare) {
      if (!this.isMyTurn || this.gameOver) {
        event.preventDefault();
        return;
      }
      event.preventDefault();
      const prevFen = this.localFen;
      const dragInfo = JSON.parse(event.dataTransfer.getData('text/plain'));
      if (!this.legalDrags.includes(targetSquare)) return;

      let castlingType = CastlingType.NOCASTLE;
      if (['K', 'k'].includes(dragInfo.piece)) {
        const startFile = dragInfo.from % 8;
        const endFile = targetSquare % 8;
        const fileDiff = Math.abs(startFile - endFile);

        if (fileDiff === 2) {
          castlingType = endFile > startFile ?
              CastlingType.LONGCASTLE : CastlingType.SHORTCASTLE;
          console.log("I've entered this castling thing!!!")
        }
      }

      this.localFen = this.applyMoveToFEN(prevFen, dragInfo.from, targetSquare, dragInfo.piece, castlingType);

      try {
        const response = await axios.post(`http://localhost:8080/api/game/${this.gameId}/move`, {
          from: dragInfo.from,
          to: targetSquare,
          piece: dragInfo.piece,
          castlingType: castlingType
        }, {
          headers: {
            Authorization: `Bearer ${this.authStore.token}`
          }
        });
        console.log("This is after regular move:", response.data);
        this.serverFen = response.data.fen;
        this.turnCounter++;

        this.checkmate = response.data.checkmate;
        this.gameOver = this.checkmate;
        console.log(this.checkmate + " it is in fact this about checkmate");


        this.lastMove = {from: dragInfo.from, to: targetSquare};

        this.checkSquare = response.data.enemyKingInCheck;
        this.legalMoves = response.data.legalMoves;
        this.castlingRights = response.data.castlingRights;
        this.enPassantSquare = response.data.enPassantSquare;
        this.highlightedSquares = [];
        this.materialCount = response.data.materialImbalance;
        if (this.isBotGame) {
          await this.getStockfishMoves();
        }
      } catch (error) {
        console.error('Move failed:', error);
        this.localFen = prevFen;
      }
    },

    applyMoveToFEN(fen, from, to, piece, castlingType) {
      const fenParts = fen.split(' ');
      const ranks = fenParts[0].split('/')
          .map(rank => rank.replace(/\d/g, n => '-'.repeat(n)).split(''));

      const fromRank = 7 - Math.floor(from / 8);
      const fromFile = 7 - (from % 8);
      const toRank = 7 - Math.floor(to / 8);
      const toFile = 7 - (to % 8);

      // Handle castling rook movement
      if (castlingType !== CastlingType.NOCASTLE) {
        const isWhite = piece === 'K';
        const kingRank = Math.floor(from / 8);
        let rookFrom, rookTo;

        if (castlingType === CastlingType.SHORTCASTLE) {
          rookFrom = kingRank * 8 + 0; // h-file
          rookTo = kingRank * 8 + 2;  // f-file
        } else {
          rookFrom = kingRank * 8 + 7; // a-file
          rookTo = kingRank * 8 + 4;   // d-file
        }

        // Convert rook positions
        const rFromRank = 7 - Math.floor(rookFrom / 8);
        const rFromFile = 7 - (rookFrom % 8);
        const rToRank = 7 - Math.floor(rookTo / 8);
        const rToFile = 7 - (rookTo % 8);

        ranks[rFromRank][rFromFile] = '-';
        ranks[rToRank][rToFile] = isWhite ? 'R' : 'r';
      }

      // Handle en passant
      if (to === this.enPassantSquare && ['P', 'p'].includes(piece)) {
        const delta = piece === 'P' ? -8 : 8;
        const capturedSquare = to + delta;
        const capRank = 7 - Math.floor(capturedSquare / 8);
        const capFile = 7 - (capturedSquare % 8);
        ranks[capRank][capFile] = '-';
      }

      // Main move
      ranks[fromRank][fromFile] = '-';
      ranks[toRank][toFile] = piece;

      // supposed pawn promotion
      if (piece === 'P' && toRank === 0) {
        ranks[toRank][toFile] = 'Q'; // Promote white pawn to queen
        this.promotionPgnAnnotation = 'Q';
      } else if (piece === 'p' && toRank === 7) {
        ranks[toRank][toFile] = 'q'; // Promote black pawn to queen
        this.promotionPgnAnnotation = 'q';
      } else {
        this.promotionPgnAnnotation = '';
      }

      // Rebuild FEN
      const newPlacement = ranks.map(rank => {
        let fenRank = '';
        let emptyCount = 0;
        for (const sq of rank) {
          if (sq === '-') {
            emptyCount++;
          } else {
            if (emptyCount > 0) fenRank += emptyCount;
            fenRank += sq;
            emptyCount = 0;
          }
        }
        if (emptyCount > 0) fenRank += emptyCount;
        return fenRank;
      }).join('/');

      return [
        newPlacement,
        fenParts[1] === 'w' ? 'b' : 'w',
        fenParts[2],
        fenParts[3],
        ...fenParts.slice(4)
      ].join(' ');
    },

    showLegalMoves(square) {
      if (!this.isMyTurn || this.gameOver) {
        this.highlightedSquares = [];
        return;
      }
      const currentTurn = this.fen.split(' ')[1];
      const pieceData = this.board.flat().find(s => s.square === square);

      if (!pieceData || pieceData.isEmpty) return;

      const pieceColor = pieceData.symbol === pieceData.symbol.toLowerCase() ? 'black' : 'white';

      if ((currentTurn === 'w' && pieceColor !== 'white') ||
          (currentTurn === 'b' && pieceColor !== 'black')) {
        this.highlightedSquares = [];
        return;
      }

      this.highlightedSquares = this.legalMoves[square] || [];
    },

    checkIsLegal(event) {
      event.preventDefault();
    }
  },

  data() {
    return {
      pieceImages: {
        // Black pieces
        'r': blackRook,
        'p': blackPawn,
        'n': blackKnight,
        'b': blackBishop,
        'q': blackQueen,
        'k': blackKing,

        // White pieces
        'R': whiteRook,
        'P': whitePawn,
        'N': whiteKnight,
        'B': whiteBishop,
        'Q': whiteQueen,
        'K': whiteKing
      },
      fen: '',
      legalMoves: {},
      highlightedSquares: [],
      legalDrags: [],
      castlingRights: 0,
      enPassantSquare: -1,
      materialCount: 0,
      pgn: '',
      turnCounter: 1,
      promotionPgnAnnotation: '',
      checkSquare: -1,
      lastMove: {from: -1, to: -1},
      checkmate: false,
      gameId: null,
      playerColor: 'WHITE',
      currentTurn: 'WHITE',
      opponentUsername: '',
      pollInterval: null,
      opponentId: null,
      localFen: '',
      serverFen: '',
      isMyTurn: false,
      difficulty: null,
      isBotGame: false,
      successeNb: 0,
      gameOver: false,
      resignation: false
    }
  },

  computed: {
    pgnLines() {
      return (this.pgn.match(/\n/g) || []).length;
    },
    board() {
      const whichFen = this.isMyTurn ? this.localFen : this.serverFen;
      try {
        const fenPosition = whichFen.split(' ')[0];
        const ranks = fenPosition.split('/');
        return ranks.map((rank, i) => {
          const expanded = rank.replace(/\d/g, num => '-'.repeat(num));
          return expanded.split('').map((symbol, fileIndex) => ({
            symbol,
            isEmpty: symbol === '-',
            color: symbol !== '-' && symbol === symbol.toLowerCase() ? 'black' : 'white',
            square: (7 - i) * 8 + (7 - fileIndex)
          }));
        });
      } catch (error) {
        console.error('Error parsing FEN:', error);
        return [];
      }
    }
  }
};
</script>

<style scoped>
.app-root {
  position: relative;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #1e293b; /* ciemne tło całej aplikacji */
  color: #f8fafc;
  min-height: 100vh;
  padding: 1rem;
  box-sizing: border-box;
}

.chess-container {
  display: flex;
  justify-content: space-between;
  gap: 1.25rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.chess-board {
  flex: 1;
  max-width: 800px;
  background: linear-gradient(145deg, #f0d9b5, #b58863);
  border-radius: 16px;
  box-shadow:
      0 8px 15px rgba(0, 0, 0, 0.25),
      inset 0 0 30px rgba(255, 255, 255, 0.15);
  padding: 12px;
  user-select: none;
  position: relative;
  transition: transform 0.6s ease;

}

.chess-board.flipped {
  transform: rotate(180deg);
}

.row {
  display: flex;
}

.square {
  width: 70px;
  height: 70px;
  position: relative;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: background-color 0.3s ease;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.2);
  /* Brak zaokrągleń - pola są kwadratowe */
}

.light {
  background-color: #f0d9b5;
}

.dark {
  background-color: #b58863;
}

.square:hover:not(.legal-move):not(.last-move):not(.check-square) {
  filter: brightness(1.1);
  box-shadow: 0 0 12px 3px rgba(255, 255, 255, 0.3);
  transition: box-shadow 0.3s ease, filter 0.3s ease;
  z-index: 10;
  pointer-events: none;
}

.legal-move::after {
  content: '';
  position: absolute;
  width: 12px;
  height: 12px;
  background-color: rgba(178, 222, 39, 1);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2; /* wyżej niż last-move */
  pointer-events: none;
  box-shadow: none;
  animation: none;
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.3);
    opacity: 0.6;
  }
}

.check-square::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 0, 0, 0.5);
  /* Brak zaokrągleń, overlay kwadratowy */
  z-index: 0;
  box-shadow: none;
  animation: none;
  pointer-events: none;
  mix-blend-mode: multiply;
}

@keyframes glowRed {
  from { box-shadow: 0 0 15px 4px rgba(220, 38, 38, 0.8); }
  to { box-shadow: 0 0 25px 8px rgba(220, 38, 38, 1); }
}

.last-move {
  position: relative;
  box-shadow: inset 0 0 12px 4px rgba(255, 255, 0, 0.3);
  /* Brak zaokrągleń */
  transition: box-shadow 0.4s ease;
}

.last-move::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 2px solid rgba(255, 255, 0, 0.4);
  z-index: 1; /* niżej niż legal-move */
  pointer-events: none !important;
  box-shadow: none;
  animation: none;
}

.piece {
  width: 60px;
  height: 60px;
  user-select: none;
  pointer-events: auto;
  transition: transform 0.35s ease, filter 0.3s ease;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.45));
  border-radius: 8px;
  will-change: transform;
  animation: fadeInScale 0.3s ease forwards;
  cursor: grab;
}

.piece.opacity-70 {
  filter: grayscale(80%) brightness(0.7) drop-shadow(0 0 2px rgba(0,0,0,0.3));
  cursor: default;
}

.chess-board.flipped .piece {
  transform: rotate(180deg) !important;
  cursor: grab;
}


@keyframes fadeInScale {
  0% {
    opacity: 0;
    transform: scale(0.6);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.turn-message-container {
  min-height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
  z-index: 5;
}

.turn-message-container.rotatore {
  transform: rotate(180deg);
}

.turn-message-container > div {
  background-color: rgba(22, 163, 74, 0.8);
  padding: 1rem 2rem;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
  box-shadow: 0 8px 15px rgba(22, 163, 74, 0.5);
  transition: all 0.3s ease;
}

.right-panel {
  width: 500px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  color: #e2e8f0;
  user-select: text;
}

.resign-button {
  background-color: #dc2626;
  color: white;
  font-weight: 700;
  border-radius: 10px;
  padding: 0.75rem 1.5rem;
  box-shadow: 0 6px 10px rgba(220, 38, 38, 0.6);
  transition: background-color 0.25s ease, box-shadow 0.25s ease;
  cursor: pointer;
  border: none;
  outline-offset: 3px;
}

.resign-button:hover {
  background-color: #b91c1c;
  box-shadow: 0 8px 15px rgba(185, 28, 28, 0.7);
}

.resign-button:active {
  background-color: #7f1d1d;
  box-shadow: 0 4px 6px rgba(127, 29, 29, 0.7);
  transform: scale(0.97);
}

.pgn-container {
  max-height: 500px;
  overflow-y: auto;
  border: 1px solid #475569;
  border-radius: 12px;
  background: #334155;
  padding: 1rem;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9rem;
  color: #f1f5f9;
  box-shadow: inset 0 0 15px rgba(255,255,255,0.05);
}

.pgn-button {
  width: 100%;
  white-space: pre-wrap;
  text-align: left;
  cursor: pointer;
  background: none;
  border: none;
  color: inherit;
  outline: none;
  user-select: text;
  transition: color 0.3s ease;
}

.pgn-button:hover {
  color: #a3bffa;
}

.scrollable-pgn {
  overflow-y: auto;
}

/* Game Over Modal */
.game-over-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: fadeIn 0.35s ease forwards;
}

.game-over-modal {
  background: #f9fafb;
  padding: 2rem 2.5rem;
  border-radius: 16px;
  max-width: 360px;
  width: 90%;
  box-shadow:
      0 10px 30px rgba(0, 0, 0, 0.25),
      inset 0 0 30px rgba(255, 255, 255, 0.6);
  position: relative;
  text-align: center;
  font-weight: 600;
  color: #111827;
  user-select: none;
  transform: scale(0.95);
  animation: scaleUp 0.3s ease forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes scaleUp {
  from { transform: scale(0.95); opacity: 0.7; }
  to { transform: scale(1); opacity: 1; }
}

.game-over-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 1.25rem;
}

.game-over-title {
  color: #ef4444;
  font-weight: 700;
  letter-spacing: 1.3px;
  user-select: none;
}

.close-button {
  font-size: 28px;
  background: none;
  border: none;
  cursor: pointer;
  color: #ef4444;
  font-weight: 700;
  line-height: 1;
  padding: 0 0.3rem;
  transition: color 0.3s ease;
}

.close-button:hover {
  color: #b91c1c;
}

.resignation-message {
  color: #b91c1c;
  font-weight: 700;
  font-size: 1rem;
}

/* Responsive */
@media (max-width: 1200px) {
  .chess-container {
    flex-direction: column;
    max-width: 100%;
  }
  .right-panel {
    width: 100%;
    order: -1;
  }
  .chess-board {
    max-width: 100%;
    margin-right: 0;
  }
}
</style>

.square.last-move.legal-move::after {
width: 8px;
height: 8px;
z-index: 2;
pointer-events: none;
}