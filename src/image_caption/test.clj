(ns image_caption.test
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals facts sentences]))


(defquery sample-facts []
  (simple-fact-prior))

(defquery sample-sentence' []
  (generate-sentence (simple-fact-prior)))

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

(with-primitive-procedures [mixture-distribution get-words dirac resample-part bag-of-words]
  (defm query-dist [sentence]
    (let [good-length (count (get-words sentence))]
      (mixture-distribution
       0.9999
       (dirac sentence)
       (mixture-distribution
        0.9
        (resample-part sentence
                       (/ 1.0 (* 2 good-length)))
        (bag-of-words (sample (normal good-length 5))
                      sentence
                      (/ 1.0 (* 2 good-length))))))))

(with-primitive-procedures [get-words dirac]
  (defquery sentence-to-facts [sentence]
    (let [facts (simple-fact-prior)
          sentence' (generate-sentence facts)
          good-length (count (get-words sentence))
          other-length (count (get-words sentence'))
          ]
      (observe (normal good-length 3) other-length)
      ;;(observe (dirac sentence) sentence')
      ;;(observe (bag-of-words (count (get-words sentence)) sentence 0.001) sentence')
      (observe (query-dist sentence) sentence')
      ;; (observe (dirac sentence) sentence')
      [facts sentence'])))


(first (drop 1500 (doquery :importance sample-facts [])))

;; (first (doquery :importance query-sentence [example-facts]))
;; (first (drop 1500 (doquery :lmh query-short-sentence [example-facts])))

;;(first (doquery :importance query-sentence [[[:kicks :boy :boy]]]))


;;(first (drop 100000 (doquery :lmh sentence-to-facts ["The bear kicks the boy, who is close to the girl."])))

;;(first (drop 100000 (doquery :lmh sentence-to-facts ["The bear kicks the boy, who kicks himself."])))



(first (doquery :importance sample-sentence' []))


