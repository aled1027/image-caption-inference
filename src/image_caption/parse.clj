(ns image_caption.parse
  (:use [clojure string])
  (:use [anglican core runtime emit stat])
  (:use [image_caption globals facts sentences]))


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
  (defm query-dist [sentence other-length]
    (let [good-length (count (get-words sentence))]
      (mixture-distribution
       ;; 0.9999
       (- 1 1e-10)
       (dirac sentence)
       (mixture-distribution
        (- 1 1e-1)
        (resample-part sentence
                       (/ 1.0 (* 2 good-length)))
        (bag-of-words other-length ;;(sample (normal good-length 5))
                      sentence
                      (/ 1.0 (* 2 good-length))))))))

(with-primitive-procedures [get-words normalize-sentence dirac]
  (defm observe-sentence [sentence sentence']
    (let [good-length (count (get-words sentence))
          other-length (count (get-words sentence'))]
      (observe (normal good-length 1) other-length)
      ;;(observe (dirac sentence) sentence')
      ;;(observe (bag-of-words (count (get-words sentence)) sentence 0.001) sentence')
      (observe (query-dist (normalize-sentence sentence) other-length) (normalize-sentence sentence'))
      ;; (observe (dirac sentence) sentence')
      )))


(defquery sentence-to-facts [sentence]
  (let [facts (simple-fact-prior)
        sentence' (generate-sentence facts)]
    (observe-sentence sentence sentence')
    [facts]))

(defquery sentence-to-pruned-facts [sentence facts]
  (let [facts (subsample-fact-prior facts)
        sentence' (generate-sentence facts)]
    (observe-sentence sentence sentence')
    [facts]))


;; (first (drop 1500 (doquery :importance sample-facts [])))


;; (first (drop 50000 (doquery :rmh sentence-to-facts ["The bear kicks the boy, who is close to the girl."])))


;;(collect-results (take 1000 (drop 50000 (doquery :rmh sentence-to-facts ["The bear kicks the boy, who is close to the girl."]))))

;;(first (drop 100000 (doquery :lmh sentence-to-facts ["The bear kicks the boy, who kicks himself."])))


(defn parse-sentence [sentence]
  (first (:result (first (drop 400000 (doquery :rmh sentence-to-facts [sentence]))))))


(defn parse-sentence' [sentence]
  (let [pruned-facts
        (vec (apply
              concat
              (apply
               concat
               (keys (collect-results
                      (apply
                       concat
                       (pmap (fn [_] (take 100 (drop 10000 (doquery :rmh sentence-to-facts [sentence]))))
                             (range 8))))))))
        ]
    (:result (first (drop 200000 (doquery :rmh sentence-to-pruned-facts [sentence pruned-facts]))))))

;; (defn parse-sentence'' [sentence]
;;   (take 5
;;         (sort-by val >
;;                  (empirical-distribution (collect-results (take 400000 (doquery :smc sentence-to-facts [sentence] :number-of-particles 50000)))))))

(defn parse-sentence'' [sentence]
  (take 5
        (sort-by val >
                 (empirical-distribution (collect-results (take 400000 (doquery :pimh sentence-to-facts [sentence] :number-of-particles 20000)))))))



;;(parse-sentence "The bear is close to the girl and the ball kicks itself.")
;;(parse-sentence "The bear faces the boy, who kicks the soccer ball.")
;;(parse-sentence "The bear kicks Bob while Bob is kicking the girl and the ball is close to the bear.")

;;(parse-sentence "Bob is kicking the girl.")

;; (first (doquery :importance sample-sentence' []))

;; (first (doquery :importance query-short-sentence [[[:kicks :bear :boy]
;;                                                    [:kicks :boy :girl]
;;                                                    [:close :soccer-ball :bear]]]))

