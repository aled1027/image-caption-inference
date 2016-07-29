(ns image_caption.images
  (:use [clojure set])
  (:use [anglican core runtime emit])
  (:use [image_caption globals]))

; Generative model from scene description to image description

(def border-x 50)
(def border-y 200)

; close position chosen by sampling pair of gaussians
(def close-offset 50) ; offset of the gaussians from the other objects position
(def close-variance 50) ; variance of each gaussin

; image descriptions are of the form
; [ ... { :sprite :boy :x 78 :y 144 :flip } ... ]

; generative model:
;   takes facts and generates image desription
;   1) which entities are present? boy, girl, soccer-ball
;   2) how to modify each entity:
;       :kicks a b => add kicking leg to a
;    3) how to place each object:
;       :kicks a b => a is close to b - set priors on positions on a and b such that they are close
;     assume facts are sensible?

; extract the noun(s) from a fact
; TODO: assumes all facts are binary
(defn nouns-from-fact [fact]
  #{(nth fact 1) (nth fact 2)}
)

; a set of all nouns from a set of facts
(defn nouns-from-facts [facts]
  (loop [nouns #{}
         facts facts]
    (if (seq facts)
      (let [new-nouns (nouns-from-fact (first facts))]
        (recur (union nouns new-nouns) (rest facts)))
      nouns)))


(defm initial-entities [nouns]
  (into {}
    (map
      (fn [noun]
        (let [x (sample (uniform-discrete 0 (- image-width border-x)))
              y (sample (uniform-discrete 0 (- image-height border-y)))
              flip (sample (uniform-discrete 0 1))]
          [noun {:sprite noun :x x :y y :flip flip}]))
      nouns)))

(defm update-entity [entities entity key value]
  (let [x (get entities entity)
        new-x (assoc x key value)]
   (assoc entities entity new-x)))


(declare apply-facts)

(defm sample-close-to [coord]
  (if (sample (flip 0.5))
    (sample (normal (- coord close-offset) close-variance))
    (sample (normal (+ coord close-offset) close-variance))))

(defm apply-fact [entities fact]
  (let [relation (nth fact 0)
        left (nth fact 1)
        right (nth fact 2)
        left-entity (get entities left)
        right-entity (get entities right)]
    (cond
      (= relation :close)
        ; right is close to left
        (let [left-x (:x left-entity)
              left-y (:y left-entity)
              x (sample-close-to left-x)
              y (sample-close-to left-y)]
            (update-entity (update-entity entities right :y y) right :x x))
      (= relation :faces)
        ; left faces right
        (let [left-x (:x left-entity)
              right-x (:x right-entity)]
          (if (< left-x right-x)
            (update-entity entities left :flip 0)
            (update-entity entities left :flip 1)))
      (= relation :kicks)
        ; left sticks leg out, and left close to right, and left faces right
        (apply-facts
          (if (= left :boy)
            (update-entity entities left :sprite :boy-kicking)
            (update-entity entities left :sprite :girl-kicking))
          #{[:close left right] [:faces left right]})
      :else
        entities)))

(defm apply-facts [entities facts]
  (if (seq facts)
    (apply-fact (apply-facts entities (rest facts)) (first facts))
    entities))


(defm generate-sprite [value]
  (let [entity (nth value 1)]
    {:sprite (get entity :sprite)
     :x (get entity :x)
     :y (get entity :y)
     :flip (get entity :flip)}))

(defm generate-sprites [entities]
  (map generate-sprite (seq entities)))


(with-primitive-procedures [nouns-from-facts]
  (defquery generate-image [facts]
    (let [
      ; the entities (the nouns mentioned in the facts)
      entities (initial-entities (nouns-from-facts facts))
      ; modify the entities using the facts
      entities (apply-facts entities facts)
      ; generate image description for the entities
      sprites (generate-sprites entities)]
    sprites)))
