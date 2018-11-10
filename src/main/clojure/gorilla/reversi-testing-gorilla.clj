;; gorilla-repl.fileformat = 1

;; **
;;; # Reversi testing
;; **

;; @@
(ns reversi-koth.gorilla
    (:require [gorilla-plot.core :as plot]
      [reversi-koth.board :as board]
      [clojure.core.matrix :as mx]
      [gorilla-renderable.core :as grender]
      [clojure.tools.namespace.repl :as ns-reload]))
;; @@

;; **
;;; ### So we can refresh the code
;; **

;; @@
(ns-reload/refresh)
;; @@

;; **
;;; This is a board view. It was built with (fun &) reference to http://gorilla-repl.org/renderer.html
;; **

;; @@

(defrecord BoardView [backing])
(defrecord PieceView [piece-type])

(defn list-like
      [data value open close separator]
      {:type :list-like
       :open open
       :close close
       :separator separator
       :items data
       :value value})

(defn piece-view [piece-type]
      {:type :html
       :content (str "<div style=\"width:30px; height:30px; border-radius:100%;
                 background:"
                     (case piece-type
                           :black "radial-gradient(circle at center, #585858, #000000)"
                           :white "radial-gradient(circle at center, #eaeaea, #b7b1b1)"
                           :hilight "green"
                           :none "transparent"
                           (pr piece-type))
                     ";\"></div>")
       :value (pr-str piece-type)})

(extend-type BoardView
             grender/Renderable
             (grender/render [self]
                             (let [contents (:backing self)
                                   rows (map (fn [r] (list-like (map piece-view r)
                                                                (pr-str r) "<tr><td>" "</td></tr>" "</td><td>")) contents)
                                   body (list-like rows
                                                   (pr-str self) "<center><table>" "</table></center>" "\n")]
                                  body)))

(defn board-view [backing] (BoardView. backing))


;; @@

;; **
;;; This is a simple demo showing that `points-reachable-from` is working correctly.
;; **

;; @@
(board-view (board/update-points-to (board/gen-empty-board 8) (apply concat (board/points-reachable-from [3 5] 8)) :hilight))
;; @@

;; **
;;; Basic board
;; **

;; @@
(board-view (board/new-game-board 8))
;; @@

;; @@
(let [b (board/new-game-board 8)
      moves (board/moves-for-player :black b)]
     (board-view (board/update-points-to b moves :hilight)))
;; @@

;; @@
(let [b (board/new-game-board 8)
      moves (board/moves-for-player :black b)
      b (board/make-move b (first moves) :black)]
     (board-view b))
;; @@

;; @@
(board/score-game (board/new-game-board 8))
;; @@

;; **
;;; Quick and dirty game impl
;; **

;; @@
(let [moves [
              [:black [3 2]]
              [:white [2 4]]
              ]
      board (board/new-game-board 8)                          ; the grid is transposed, so this helps easily translate to screen cords without affecting the game
      board (reduce (fn [b [player point]] (board/make-move b (reverse point) player)) board moves)]
  (board-view board))
;; @@
