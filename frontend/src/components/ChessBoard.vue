<template>
  <div class="chess-board">
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
    <div class="flex items-center justify-center">Is the game over: {{ checkmate === true ? ' yes' : ' false' }}</div>
    <div class="flex items-center justify-center">King check square: {{checkSquare}}</div>
    <div class="flex items-center justify-center">Material: {{ materialCount }}</div>
    <div class="flex items-center justify-center"><button class="w-full max-w-xs break-words text-center px-4 py-2" @click="copyPgnToClipboard">{{ pgn }}</button></div>
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


const CastlingType = {
  SHORTCASTLE: 'SHORTCASTLE',
  LONGCASTLE: 'LONGCASTLE',
  NOCASTLE: 'NOCASTLE'
};

export default {
  mounted() {
    this.fetchBoard();
    this.fetchLegalMoves();
  },
  methods: {
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
    async fetchBoard() {
      try {
        const response = await axios.get('http://localhost:8080/api/test');
        this.fen = response.data;
      } catch (error) {
        console.error('Error fetching FEN:', error);
      }
    },

    async fetchLegalMoves() {
      try {
        const response = await axios.get('http://localhost:8080/api/test/getLegals');
        this.legalMoves = Object.entries(response.data).reduce((acc, [key, value]) => {
          acc[parseInt(key)] = value;
          return acc;
        }, {});
      } catch (error) {
        console.error('Error getting legal moves:', error);
      }
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
      event.preventDefault();
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

      this.fen = this.applyMoveToFEN(
          dragInfo.originalFEN,
          dragInfo.from,
          targetSquare,
          dragInfo.piece,
          castlingType
      );

      try {
        const response = await axios.post('http://localhost:8080/api/test', {
          from: dragInfo.from,
          to: targetSquare,
          piece: dragInfo.piece,
          castlingType: castlingType
        });
        this.turnCounter++;

        this.checkmate = response.data.checkmate;
        console.log(this.checkmate + " it is in fact this about checkmate");

        this.lastMove = { from: dragInfo.from, to: targetSquare };
        if(this.turnCounter%2 === 0){this.pgn += this.turnCounter/2 + "."}
        this.pgn += " " + this.chessPositionIntegerToSquare(dragInfo.from) + this.chessPositionIntegerToSquare(targetSquare) + this.promotionPgnAnnotation;
        if(this.turnCounter%2 !== 0){this.pgn += "\n";}

        this.checkSquare = response.data.enemyKingInCheck;
        this.legalMoves = response.data.legalMoves;
        this.castlingRights = response.data.castlingRights;
        this.enPassantSquare = response.data.enPassantSquare;
        this.highlightedSquares = [];
        this.materialCount = response.data.materialImbalance;
      } catch (error) {
        console.error('Move failed:', error);
        this.fen = dragInfo.originalFEN;
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
    }
  },

  computed: {
    board() {
      if (!this.fen) return [];
      try {
        const fenPosition = this.fen.split(' ')[0];
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
.game-over {
  pointer-events: none;
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
</style>