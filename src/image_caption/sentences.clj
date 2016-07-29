(ns image_caption.sentences
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals facts]))

(def translations
  {;; nouns
   :boy ["Bob" "the boy"]
   :girl ["Alice" "the girl"]
   :soccer-ball ["the soccer ball" "the ball"]
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

(def example-facts
  [[:kicks :girl :boy]
   [:faces :bear :girl]
   [:kicks :boy :soccer-ball]
   [:close :bear :soccer-ball]])

(mapv reorder-fact example-facts)

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
                          ;; (if (and prev (= (get cur 2) (get prev 2)))
                          ;;   [(accusative-pronoun (get cur 2))]
                          ;;   nil) ;; creates awkward sentences
                          )]
                     (into [] (map sample-from-vector [translations-0 translations-1 translations-2]))))
          result (smart-join (apply concat (map handle (triples (map reorder-fact facts)))))]
      result)))

(defn capitalize-sentence [string]
  (str (upper-case (first string)) (subs string 1)))

(defn sentenceify [string]
  (capitalize-sentence (str string ".")))

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
