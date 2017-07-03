(ns reversi-koth.board
  (:require [clojure.spec :as s]))

; get-in board [x y]

(s/def ::piece #{:none :white :black})
(s/def ::board (fn [b]
                 (and (vector? b)
                      (every? vector? b)
                      (every? (partial s/valid? ::piece) (flatten b))
                      (let [len (count b)]
                        (every? #(= len (count %)) b)))))

(defn- vecify [board]
  "convert board from list form to vec form, i.e. ((:empty)) to [[:empty]]"
  (mapv #(mapv identity %) board))

(defn opposite-player [player]
  (case player
    :white :black
    :black :white
    :none :none))

(defn size-of [board]
  (count board))

(def cardnal-directions
  (for [horiz [1 0 -1]
        vert [1 0 -1]
        :when (and (not= 0 horiz vert))]
    [horiz vert]))

(defn in-board? [point board-size]
  (let [[x y] point]
    (and (<= 0 x)
         (<= 0 y)
         (> board-size x)
         (> board-size y))))

(defn tag-board-with-cords [board]
  "Adds the x/y cords to a board. Results in the form of
  [
   [[[x y] piece]
    [[x y] piece]]
  ]"
  (map-indexed (fn [x column]
                 (map-indexed (fn [y piece]
                                [[x y] piece]) column)) board))

(defn get-in-board [board point]
  (get-in (vecify board) point))

(defn gen-empty-board [side-length]
  "Board filled with `:none`"
  (vecify (repeat side-length (repeat side-length :none))))

(defn locations-on-board-in-direction [origin direction board-size]
  "All the points on the board from the origin in the given direction."
  (take-while #(in-board? % board-size)
              (iterate
                (fn [[x y]] [(+ x (first direction)) (+ y (second direction))])
                origin)))

(defn points-reachable-from [origin board-size]
  "List of rays reachable from origin. `origin` is always first in results."
  (map #(locations-on-board-in-direction origin % board-size) cardnal-directions))

(defn update-point-to [board point new-type]
  (assoc-in board point new-type))

(defn update-points-to [board points new-type]
  "Same as `update-point-to`, but takes a list of points"
  (reduce #(update-point-to %1 %2 new-type) board points))

(defn new-game-board [side-length]
  "Setup a new board with the inital locations filled."
  (let [center (/ side-length 2)]
    (-> (gen-empty-board side-length)
        (update-point-to [(- center 1) (- center 1)] :white)
        (update-point-to [(- center 1) center] :black)
        (update-point-to [center (- center 1)] :black)
        (update-point-to [center center] :white))))


(defn filter-board [predicate board]
  "Filter a board and return positions that match the predicate.
   `predicate` is called as `(p piece-type position)`"
  (let [positions (apply concat (tag-board-with-cords board))]
    (map first (filter (fn [[idx piece]] (predicate piece idx)) positions))))

(defn moves-for-player [player board]
  "Determin all valid moves for `player`"
  (let [player-posititions (filter-board (fn [piece idx] (= piece player)) board)
        possible-moves (mapcat #(points-reachable-from % (size-of board)) player-posititions)
        valid-moves (mapcat (fn [move-ray]
                       (let [[contained chopped] 
                             (split-with 
                               #(= (opposite-player player) (get-in-board board %)) 
                               (drop 1 move-ray))]
                         (if (and (not-empty contained)
                                  (= :none (get-in-board board (first chopped))))
                           [(first chopped)]
                           []))) possible-moves)]
    valid-moves))


(defn make-move [board point player]
  (let [possible-capture-rays (points-reachable-from point (size-of board))
        captured-points (mapcat 
                          (fn [capt-ray]
                            (take-while #(= (opposite-player player) (get-in-board board %))
                                        (drop 1 capt-ray)))
                          possible-capture-rays)]
    (update-points-to board (conj captured-points point) player)))

(defn score-game [board]
  (map (fn [player] 
         [player
          (count (filter-board 
                   (fn [type _] (= type player))
                   board))])
       [:black :white]))
