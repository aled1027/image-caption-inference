(ns image_caption.sentences
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals facts]))

(def translations
  {;; nouns
   :boy ["Bob" "the boy"]
   :girl ["Alice" "the girl"]
   :soccer-ball ["the ball" "the soccer ball"] 
   :bear ["the bear" "the animal"]
   ;; verbs
   :close ["is close to"]
   :faces ["faces" "is facing"]
   :kicks ["kicks" "is kicking"]
   ;; passive verbs
   [:passive :close] ["is close to"]
   [:passive :faces] ["is faced by" "is being faced by"]
   [:passive :kicks] ["is kicked by" "is being kicked by"]
   ;; other
   :and ["and" "while"]})

(defn pronoun [noun]
  (cond (= noun :boy) "he"
        (= noun :girl) "she"
        :else "it"))

(def is-person
  {:boy true
   :girl true})

(defn relative-pronoun [noun]
  (if (noun is-person) "who" "which"))

(defn accusative-pronoun [noun]
  (cond (= noun :boy) "him"
        (= noun :girl) "her"
        :else "it"))

(defn reflexive-pronoun [noun]
  (cond (= noun :boy) "himself"
        (= noun :girl) "herself"
        :else "itself"))

(defdist shuffled
  "Uniform random permutation of the given vector."
  [vector] []
  (sample* [this] (shuffle vector))
  (observe* [this value]            
            (if (= (sort vector)
                   (sort value))
              (sum (map (comp - log) (range 1 (inc (count vector)))))
              Double/NEGATIVE_INFINITY)))

;; (defn add-article [noun]
;;   ;; TODO: other articles
;;   ;; TODO: enumerate animals? E.g. "the first bear"
;;   (if (not (noun is-person))
;;     [[:the noun]]
;;     [noun]))

(defn reorder-fact [fact]
  (let [verb (get fact 0)
        nouns (subvec fact 1)]
    (if (= 1 (:arity (get verbs verb)))
      (concatv (get nouns 0) [verb])
      (concatv [(get nouns 0)] [verb] [(get nouns 1)]))))

;; (mapv reorder-fact example-facts)

(defn triples [list]
  (mapv vec (partition 3 1 (concat [nil] list [nil]))))

(defn smart-join [strings]
  (loop [cur nil
         strings strings]
    (if (seq strings)
      (let [compute-sep (fn [a b] (if (or (not cur) (= \, (first b))) "" " "))
            sep (compute-sep cur (first strings))]
        (recur (str cur sep (first strings))
               (rest strings)))
      cur)))

(with-primitive-procedures
  [concatv pronoun reorder-fact triples relative-pronoun accusative-pronoun reflexive-pronoun smart-join]
  (defm sample-sentence [facts]
    (let [pick-word (fn [word] (sample-from-vector (get translations word)))
          pick-noun (mem (fn [noun] (sample-from-vector (get translations noun))))
          connect (fn [prev word next can-use-comma]
                    (if prev
                      (str (if (and next can-use-comma)
                             ", "
                             (str (pick-word :and) " "))
                           word) ;; TODO: other connectives
                      word))
          handle (fn [[prev cur next]]
                   (let [translations-0
                         (concatv
                          [(connect prev (pick-noun (get cur 0)) next true)]
                          (if (and prev (= (get cur 0) (get prev 0)))
                            [(connect prev (pronoun (get cur 0)) next false)]
                            nil)
                          (if (and prev (= (get cur 0) (get prev 2)))
                            [(str ", " (relative-pronoun (get cur 0)))]
                            nil))

                         translations-1
                         [(pick-word (get cur 1))]

                         translations-2
                         (concatv
                          [(pick-noun (get cur 2))]
                          (if (= (get cur 0) (get cur 2))
                            [(reflexive-pronoun (get cur 0))]
                            nil)
                          (if (and prev (not= (get cur 0) (get cur 2)) (= (get cur 2) (get prev 2)) (sample (flip 0.1)))
                            [(accusative-pronoun (get cur 2))]
                            nil) ;; flip(0.1) because this often creates awkward sentences
                          )]
                     (into [] (map sample-from-vector [translations-0 translations-1 translations-2]))))
          result (smart-join (apply concat (map handle (triples (map reorder-fact facts)))))]
      result)))

(defn capitalize-sentence [string]
  (str (upper-case (first string)) (subs string 1)))

(defn sentenceify [string]
  (capitalize-sentence (str string ".")))



(defn normalize-sentence [sentence]
  (lower-case (apply str (re-seq #"[^,.]" sentence))))

(defn make-passive [fact]
  [[:passive (get fact 0)] (get fact 2) (get fact 1)])

(with-primitive-procedures [make-passive]
  (defm shuffle-passive [fact]
    (if (and (= 3 (count fact)) (sample (flip 0.3)))
      (make-passive fact)
      fact)))

(with-primitive-procedures [shuffled sentenceify]
  (defm generate-sentence [facts]
    (let [facts' (sample (shuffled facts))
          facts'' (map shuffle-passive facts')
          sentence (sample-sentence facts'')
          sentence' (sentenceify sentence)
          ]
      sentence')))

(defn get-words [sentence]
  (let [sentence (if (= \. (last sentence))
                   (subs sentence 0 (dec (count sentence)))
                   sentence)
        words (vec (split sentence #" "))
        init (first words)]
    (assoc words 0
           (if (contains? #{"Bob" "Alice"} init)
             init
             (lower-case init)))))

;; (get-words "The bear is kicking itself.")

(def all-words (vec (set (apply concat (apply concat (map #(map get-words %) (vals translations)))))))

(defm sample-random-word []
  (sample-from-vector all-words))

(defn sample*-random-word []
  (sample*-from-vector all-words))

(defdist bag-of-words [word-count sentence other-p] []
  (sample* [this]
           (let [words (get-words sentence)]
             (sentenceify (smart-join (repeatedly word-count sample*-random-word))))) ;; (wrong)
  (observe* [this value] ;; TODO: check punctuation
            (let [good-words (get-words sentence)
                  num-good (count good-words)
                  word-freq (frequencies good-words)
                  words (get-words value)
                  log-likelihood
                  (fn [word]
                    (if (contains? word-freq word)
                      (- (log (word-freq word))
                         (log num-good))
                      (log other-p)))]
              (if (= (count words) word-count)
                (sum (map log-likelihood words))
                Double/NEGATIVE_INFINITY))))

(defdist resample-part [sentence p] []
  (sample* [this]
           (let [words (get-words sentence)]
             (sentenceify
              (smart-join
               (map (fn [word]
                      (if (sample* (flip p))
                        (sample*-random-word)
                        word)) words)))))
  (observe* [this value]
            (let [words (get-words sentence)
                  words' (get-words value)
                  good-logp (log (- 1 p))
                  bad-logp (- (log p) (log (count all-words)))]
              (+ (sum (map (fn [word word']
                             (if (= word word')
                               good-logp
                               bad-logp))
                           words words'))
                 (* bad-logp (abs (- (count words) (count words'))))
                 ))))

;;(contains? {1 2 3 4} 2)
;;(/ 1.0 2)
;; (observe* (bag-of-words 4 "Bob kicks the bear." 0.1) "Bob kicks the bear.")
