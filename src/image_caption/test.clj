(ns image_caption.test
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals facts sentences]))


(defquery sample-facts []
  (simple-fact-prior))

(defquery query-sentence [facts]
  (generate-sentence facts))

(defquery query-short-sentence [facts]
  (let [r (generate-sentence facts)]
    (observe (exponential 30) (count r))
    r))

(defdist dirac
  "Dirac distribution"
  [x]  ; distribution parameters
  []   ; auxiliary bindings
  (sample* [this] x)
  (observe* [this value] (if (= x value) 0.0 (- (/ 1.0 0.0)))))

(with-primitive-procedures [dirac]
  (defquery sentence-to-facts [sentence]
    (let [facts (simple-fact-prior)
          sentence' (generate-sentence facts)]
      (observe (dirac sentence) sentence')
      facts)))


(first (drop 1500 (doquery :importance sample-facts [])))

;; (first (doquery :importance query-sentence [example-facts]))
;; (first (drop 1500 (doquery :lmh query-short-sentence [example-facts])))

;;(first (doquery :importance query-sentence [[[:kicks :boy :boy]]]))


(first (doquery :importance sentence-to-facts ["Bob kicks himself and the ball is facing itself."]))



