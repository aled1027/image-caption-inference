(ns image_caption.images
  (:use [clojure set])
  (:use [anglican core runtime emit])
  (:use [image_caption globals]))

; Generative model from scene description to image description

; image descriptions are of the form
; [ ... { :sprite :boy :x 78 :y 144 } ... ]

; generative model:
;   takes facts and generates image desription
;   1) which entities are present? boy, girl, soccer-ball
;   2) how to modify each entity:
;      :kicks a b => add kicking leg to a
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

; generate an entitiy for a noun at a uniform random position
(defm generate-entity [noun x-mean x-var y-mean y-var]
  {:sprite noun
   :x (sample (normal x-mean x-var))
   :y (sample (normal y-mean y-var))}
)

(with-primitive-procedures [nouns-from-facts]
  (defquery generate-image [facts]
    (let [
      ; get all entities (the nouns mentioned in the facts)
      entities (nouns-from-facts facts)
      ; generate image description
      image-description (map #(generate-entity % 100 5 100 5) entities)
      ; TODO: add more advanced cases, as listed above
  ] image-description)))
