(ns image_caption.sentences
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals]))

(def translations
  {;; nouns
   :boy ["Bob" "the boy"]
   :girl ["Alice" "the girl"]
   :soccer-ball ["the soccer ball" "the ball"]
   :bear ["the bear" "the animal"]
   ;; verbs
   :close ["is close to"]
   :faces ["faces" "is facing"]
   :kicks ["kicks" "is kicking"] ;; TODO: passive constructions?
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

(defdist shuffled
  "Uniform random permutation of the given vector."
  [vector] []
  (sample* [this] (shuffle vector))
  (observe* [this value]            
            (if (= (sort vector)
                   (sort value))
              (sum (map (comp - log) (range 1 (inc (count vector)))))
              Double/NEGATIVE_INFINITY)))


(defn concatv [& args]
  (into [] (apply concat args)))

;; (defn add-article [noun]
;;   ;; TODO: other articles
;;   ;; TODO: enumerate animals? E.g. "the first bear"
;;   (if (not (noun is-person))
;;     [[:the noun]]
;;     [noun]))

(defn reorder-fact [fact]
  (let [verb (fact 0)
        nouns (subvec fact 1)]
    (if (= 1 (:arity (verb verbs)))
      (concatv (nouns 0) [verb])
      (concatv [(nouns 0)] [verb] [(nouns 1)]))))

(def example-facts
  [[:kicks :girl :boy]
   [:faces :bear :girl]
   [:kicks :boy :soccer-ball]
   [:close :bear :soccer-ball]])

(mapv reorder-fact example-facts)

(defn triples [list]
  (mapv vec (partition 3 1 (concat [nil] list [nil]))))

(defm sample-from-vector [vector]
  (if (not= 0 (count vector))
    (let [i (sample (uniform-discrete 0 (count vector)))]
      (get vector i))
    nil))

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
  [concatv pronoun reorder-fact triples relative-pronoun accusative-pronoun smart-join]
  (defm sample-sentence [facts]
    (let [connect (fn [prev translations next can-use-comma]
                    (if prev
                      (map #(str (if (and next can-use-comma)
                                   ", "
                                   "and ")
                                 %) translations) ;; TODO: other connectives
                      translations))
          handle (fn [[prev cur next]]
                   (let [translations-0
                         (concatv
                          (connect prev (get translations (get cur 0)) next true)
                          (if (and prev (= (get cur 0) (get prev 0)))
                            (connect prev [(pronoun (get cur 0))] next false)
                            nil)
                          (if (and prev (= (get cur 0) (get prev 2)))
                            [(str ", " (relative-pronoun (get cur 0)))]
                            nil))

                         translations-1
                         (get translations (get cur 1))

                         translations-2
                         (concatv
                          (get translations (get cur 2))
                          ;; (if (and prev (= (get cur 2) (get prev 2)))
                          ;;   [(accusative-pronoun (get cur 2))]
                          ;;   nil) ;; creates awkward sentences
                          )]
                     (into [] (map sample-from-vector [translations-0 translations-1 translations-2]))))
          result (smart-join (apply concat (map handle (triples (map reorder-fact facts)))))]
      result)))

(defn sentenceify [string]
  (str string ".")) ;; TODO: capitalize first character

(with-primitive-procedures [shuffled sentenceify]
  (defm generate-sentence [facts]
    (let [facts' (sample (shuffled facts))
          sentence (sample-sentence facts')
          sentence' (sentenceify sentence)
          ]
      sentence')))

(defquery query-sentence [facts]
  (generate-sentence facts))

(defquery query-short-sentence [facts]
  (let [r (generate-sentence facts)]
    (observe (exponential 1) (count r))
    r))

;; (take 100 (doquery :importance query-sentence [example-facts]))

;; (first (drop 1500 (doquery :lmh query-short-sentence [example-facts])))

