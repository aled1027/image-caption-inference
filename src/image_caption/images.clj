(ns image_caption.images
  (:use [clojure set])
  (:use [anglican core runtime emit])
  (:use [image_caption globals facts images_renderer images_similarity]))

; Generative model from scene description to image description

(def border-top 20)
(def border-bottom 150)
(def border-left 50)
(def border-right 50)
(def image-background
  {:sprite :background :x (/ image-width 2) :y (/ image-height 2) :flip 0 :scale 3})

; close position chosen by sampling pair of gaussians
(def close-x-offset 80) ; offset of the gaussians from the other objects position
(def close-x-variance 20) ; variance of each gaussian
(def close-y-variance 10)

; image descriptions are of the form
; [ ... { :sprite :boy :x 78 :y 144 :flip 1 :scale 4 } ... ]

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
  (into #{} [(nth fact 1) (nth fact 2)]))

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
            (let [x (sample (uniform-continuous border-left (- image-width border-right)))
                  y (sample (uniform-continuous
                              (+ border-top (/ image-height 2))
                              (- image-height border-bottom)))
                  flip (sample (uniform-discrete 0 1))
                  scale 1]
              [noun {:sprite noun :x x :y y :flip flip :scale scale}]))
          nouns)))

(defm update-entity [entities entity key value]
  (let [x (get entities entity)
        new-x (assoc x key value)]
    (assoc entities entity new-x)))

(declare apply-facts)

(defm sample-close-x-to [coord]
  (if (sample (flip 0.5))
    (sample (normal (- coord close-x-offset) close-x-variance))
    (sample (normal (+ coord close-x-offset) close-x-variance))))

(defm sample-close-y-to [coord]
  (sample (normal coord close-y-variance)))

(defm apply-fact [entities fact]
  (let [relation (nth fact 0)
        left (nth fact 1)
        right (nth fact 2)
        left-entity (get entities left)
        right-entity (get entities right)]
    (cond
      (= left right)
      ; ignore relations for the same entity
      entities
      (= relation :close)
      ; right is close to left
      (let [left-x (:x left-entity)
            left-y (:y left-entity)
            x (sample-close-x-to left-x)
            y (sample-close-y-to left-y)]
        (update-entity (update-entity entities right :y y) right :x x))
      (= relation :faces)
      ; left faces right
      (let [left-x (:x left-entity)
            right-x (:x right-entity)]
        (if (< left-x right-x)
          (update-entity entities left :flip 0)
          (update-entity entities left :flip 1)))
      ;(= relation :kicks)
      ;; left sticks leg out, and left close to right, and left faces right
      ;(apply-facts
      ;  (cond
      ;    (= left :boy)
      ;    (update-entity entities left :sprite :boy-kicking)
      ;    (= left :girl)
      ;    (update-entity entities left :sprite :girl-kicking)
      ;    (= left :soccer-ball)
      ;    (update-entity entities left :sprite :soccer-ball-kicking)
      ;    :else
      ;    (update-entity entities left :sprite :bear-kicking))
      ;  ; FIXME: ordering here matters! Also between results
      ;  #{[:close left right] [:faces left right]})
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
     :flip (get entity :flip)
     :scale (get entity :scale)}))

(defm generate-sprites [entities]
  (conj
    (map generate-sprite (seq entities))
    image-background))

; distribution of score values given an image
(defdist image-distribution
  [img1]
  [img1pixels (get-greyscale-pixels img1)]
  (sample* [this] (assert false "can't sample from this - dummy!"))
  (observe* [this img2]
            (- (image-distance img1 img2))))

; generate an image from some facts
(with-primitive-procedures [nouns-from-facts render]
  (defm generate-image-from-facts [facts]
    (let [
          ; the entities (the nouns mentioned in the facts)
          entities (initial-entities (nouns-from-facts facts))
          ; modify the entities using the facts
          entities (apply-facts entities facts)
          ; generate image description for the entities
          sprites (generate-sprites entities)
          ; render image from the sprites
          image (render sprites)]
      image)))

; query to generate an image from some facts
(defquery generate-image-from-facts-query [facts]
  (generate-image-from-facts facts))

; generative model to find the facts for an image
(with-primitive-procedures [image-distribution]
  (defquery generate-image [image]
    (let [facts (simple-fact-prior)
          generated-image (generate-image-from-facts facts)]
      (observe (image-distribution generated-image) image)
      {:facts facts :image generated-image})))
