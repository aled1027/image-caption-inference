(ns image_caption.test
  (:use [clojure string])
  (:use [anglican core runtime emit stat])
  (:use [image_caption globals facts sentences parse images images_renderer]))



(let [facts (into #{} (parse-sentence "The ball kicks Bob while the bear is faced by Alice."))
      sample (try (first (take 1 (doquery :importance generate-image-from-facts-query [facts]))) (catch Exception e nil))
      ]
  (if sample (save-image (:result sample) "output.png"))
  facts)

(defquery sample-facts []
  (simple-fact-prior))

(defquery sample-sentence' []
  (generate-sentence (simple-fact-prior)))

(defquery query-sentence [facts]
  (generate-sentence facts))

(def example-facts
  [[:kicks :girl :boy]
   [:faces :bear :girl]
   [:kicks :boy :soccer-ball]
   [:close :bear :soccer-ball]])


;; (first (doquery :importance query-sentence [example-facts]))
;; (first (drop 1500 (doquery :lmh query-short-sentence [example-facts])))

;;(first (doquery :importance query-sentence [[[:kicks :boy :boy]]]))



(first (doquery :importance query-sentence [[[:faces :bear :girl] [:close :boy :bear]]]))

;;(first (drop 1500 (doquery :lmh query-short-sentence [[[:kicks :girl :girl] [:close :bear :girl] [:faces :bear :girl]]])))

(first (doquery :importance query-short-sentence [[[:kicks :girl :girl] [:close :bear :girl] [:faces :bear :girl]]]))

