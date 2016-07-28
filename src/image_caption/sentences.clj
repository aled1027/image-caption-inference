(ns image_caption.sentences
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals]))

(def noun-translations
  {:boy ["Bob"]
   :girl ["Alice"]
   :soccer-ball ["soccer ball"]
   :bear ["bear"]})

(def requires-article
  {:boy false
   :girl false
   :soccer-ball true
   :bear true})

(def verb-translations
  {:close ["is close to"]
   :faces ["faces" "is facing"]
   :kicks ["kicks" "is kicking"]})

(defdist shuffled
  "Uniform random permutation of the given vector."
  [vector] []
  (sample* [this] (shuffle vector))
  (observe* [this value]            
            (if (= (sort vector)
                   (sort value))
              (sum (map (comp - log) (range 1 (inc (count vector)))))
              Double/NEGATIVE_INFINITY)))

(defn add-article [noun]
  ;; TODO: other articles
  ;; TODO: enumerate animals? E.g. "the first bear"
  (if (noun requires-article)
    "the "
    ""))

(defn translate-fact [fact]
  (let [verb (get fact 0)
        nouns (subvec fact 1)
        verb' (get (verb verb-translations) 0)
        noun' (fn [i]
                (let [noun (get nouns i)
                      noun' (get (noun noun-translations) 0)]
                  (str (add-article noun) noun')))]
    (if (= 1 (:arity (verb verbs)))
      (str (noun' 0) " " verb')
      (str (noun' 0) " " verb' " " (noun' 1)))))

(defn sentenceify [string]
  (str (capitalize string) "."))

(defn facts-to-sentence
  "Simple baseline for sentence generation."
  ;; TODO: randomize
  ;; TODO: combine sentences along identical nouns?
  ;;       (E.g. "The bear faces Alice who is kicking Bob".)
  [facts]
  (sentenceify
   (let [parts (mapv translate-fact facts)]
     (reduce (fn [a b] (str a " and " b)) parts))))

(def example-facts
  [[:kicks :girl :boy]
   [:faces :bear :girl]])

(facts-to-sentence example-facts)



(with-primitive-procedures [shuffled]
  (defquery generate-sentence [facts]
    (let [facts' (sample (shuffled facts))
          ] ;; TODO: finish
      )))

(take 100 (doquery :importance generate-sentence [example-facts]))
