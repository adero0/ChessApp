<template>
  <div class="chess-container">
  <div class="game-info">
<!--    <div>Playing as: {{ playerColor }}</div>-->
<!--    <div>Opponent: {{ opponentUsername }}</div>-->
<!--    <div>Current turn: {{ currentTurn }}</div>-->
<!--    <div class="flex items-center justify-center">Is the game over: {{ checkmate === true ? ' yes' : ' false' }}</div>-->
<!--    <div class="flex items-center justify-center">King check square: {{checkSquare}}</div>-->
<!--    <div class="flex items-center justify-center">Material: {{ materialCount }}</div>-->
    <div class="xddd flex items-center justify-center overflow-y-hidden whitespace-pre-wrap max-h-50 "><button class="w-full max-w-xs break-words text-center px-4 py-2 flex-wrap" @click="copyPgnToClipboard">{{ pgn }}</button></div>
  </div>

  <div class="chess-board" :class="{'flipped': playerColor === 'BLACK' }">
    <div class="bg-green-600 bg-opacity-70 text-white p-4 rounded" v-bind:class="{'rotatore': playerColor === 'BLACK'}" v-if="isMyTurn"> Twoja kolej!</div>
    <div v-if="checkmate" class="game-over-overlay">
      <div class="game-over-message">GAME OVER</div>
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
          @dragover.prevent
          @drop="handleDrop($event, square.square)"
      >
        <img
            v-if="!square.isEmpty"
            :src="pieceImages[square.symbol]"
            :alt="square.symbol"
            class="piece"
            @click="showLegalMoves(square.square)"
            @dragstart="handleDragStart(square, $event)"
            @dragend="checkIsLegal($event)"
        >
      </div>
    </div>
  </div>
  <button
      @click="confirmResignation"
      class="bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded shadow hover:shadow-md transition duration-200 flex"
  >
    Poddaj partię
  </button>
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
    if(this.isBotGame) {
      this.difficulty = this.$route.query.difficulty;
      this.getLegalsOrMakeBotMove();
    }
    else if (this.gameId) {
        this.startGamePolling();
    } else {
      console.error("this is pretty bad")
    }
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return { authStore, router };
  },
  methods: {
    confirmResignation() {
      if (confirm("Czy napewno poddać partię?")) {
        this.resignGame();
      }
    },
    resignGame() {
      try {
        const response = axios.post(`http://localhost:8080/api/game/${this.gameId}/resign`,{},{
          headers: {
            Authorization: `Bearer ${this.authStore.token}`
          }});
      } catch(error){
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
          this.pgn = status.pgn;

          if (status.isCheckmate) {
            this.checkmate = status.isCheckmate;
            this.pgn+='#';
            clearInterval(this.pollInterval); }


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
      }, 2000); // Poll every 2 seconds
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
      } catch($e) {
        alert('Cannot copy');
      }
    },
    chessPositionIntegerToSquare(square) {
      if (!(square >= 0 && square <= 63)) {
        throw new Error("Incorrect square value")
      }
      let square_name = ''
      square_name += String.fromCharCode(((square ^ 7) % 8)+ 97);
      square_name += Math.floor(square / 8 + 1);
      return square_name;
    },
    chessPositionSquareToInteger(square_character) {
      let number = 104 - square_character.charCodeAt(0);
      number += ( square_character.charAt(1) - 1 ) * 8;
      return number;
    },

    beforeUnmount() {
      clearInterval(this.pollInterval);
    },

    async getLegalsOrMakeBotMove() {
      try {
        const response = await axios.get(`http://localhost:8080/api/game/bot_game/${this.gameId}/setup`,
            { headers: {
                Authorization: `Bearer ${this.authStore.token}`
              } }
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
          params: { fen: this.serverFen, depth: this.difficulty }
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
        }, { headers: { Authorization: `Bearer ${this.authStore.token}` } });
        console.log(response.data);
        this.serverFen = response.data.fen;
        this.localFen = response.data.fen;
        this.fen = response.data.fen;
        this.turnCounter++;
        this.pgn = response.data.pgn;
        this.checkmate = response.data.checkmate;
        this.lastMove = { from: from, to: to };
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
      if (this.checkmate) {
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
      if (!this.isMyTurn) {
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
          }});
        console.log("This is after regular move:", response.data);
        this.serverFen = response.data.fen;
        this.turnCounter++;

        this.checkmate = response.data.checkmate;
        console.log(this.checkmate + " it is in fact this about checkmate");


        this.lastMove = { from: dragInfo.from, to: targetSquare };

        this.checkSquare = response.data.enemyKingInCheck;
        this.legalMoves = response.data.legalMoves;
        this.castlingRights = response.data.castlingRights;
        this.enPassantSquare = response.data.enPassantSquare;
        this.highlightedSquares = [];
        this.materialCount = response.data.materialImbalance;
        if(this.isBotGame){
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
        this.promotionPgnAnnotation='Q';
      } else if (piece === 'p' && toRank === 7) {
        ranks[toRank][toFile] = 'q'; // Promote black pawn to queen
        this.promotionPgnAnnotation='q';
      }
      else {
        this.promotionPgnAnnotation='';
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
      if (!this.isMyTurn) {
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
      lastMove: { from: -1, to: -1 },
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
    }
  },

  computed: {
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
.game-over-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.game-over-message {

  color: white;
  font-size: 5rem;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}
.game-over.flipped {
  pointer-events: none;
  transform: rotate(180deg);
}

.last-move::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 0, 0.3);
  z-index: 0;
}

.check-square::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 0, 0, 0.5);
  z-index: 0;
}

.chess-board {
  border: 2px solid #555;
  display: inline-block;
  position: relative;
  left: 35%;
  z-index: 1;
}

.row {
  display: flex;
}

.square {
  width: 50px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.light {
  background-color: #f0d9b5;
}

.dark {
  background-color: #b58863;
}

.piece {
  position: relative;
  z-index: 1;
  width: 40px;
  height: 40px;
  user-select: none;
  pointer-events: auto;
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
  z-index: 1;
}

.chess-board.flipped {
  transform: rotate(180deg);
}

.chess-board.flipped .row {
  //flex-direction: row-reverse;
}

.piece {
  transition: transform 0.3s;
}

.chess-board.flipped .piece {
  transform: rotate(180deg);
  flex-direction: row;
}

.rotatore {
  transform: rotate(180deg);
}


</style>
<!--.xddd {
  flex-direction: row;
  display: flex;
}

.game-info {
  display: flex;
  flex-direction: row;
}-->