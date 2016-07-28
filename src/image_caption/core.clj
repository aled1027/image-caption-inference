(ns image_caption.core
  (:use [image_caption globals images sentences clipart])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:gen-class))

(defn vector-dot [xs ys]
  ;; computes the dot product between two vectors
  map (fn [x y] (* x y)) xs ys)

(defn normalize [xs]
  ;; normalizes a vector
  (let [squared (vector-dot xs xs)
        summed (reduce + squared)
        square-root (sqrt summed)]
    (map (fn [x] (/ x square-root)) xs)))

(defn image-similarity [img1 img2] 
  ;; computes absolute value of cosine distance between two images
  ;; i.e. a value between 0 and 1
  (let [img1 (flatten img1)
        img2 (flatten img2)
        numer (reduce + (vector-dot img1 img2))
        denom1 (sqrt (reduce + (vector-dot img1 img1)))
        denom2 (sqrt (reduce + (vector-dot img2 img2)))
        denom (* denom1 denom2)
        final (abs (/ numer denom))]
    final))

(defn dist-test []
  (let [img1 (render (nth all-clips 0))
        img2 (render (nth all-clips 1))]
    (println (image-similarity img1 img2))))

;(defn alex-ledger-func []
;  (println "running alex-ledger-func")
;  (println (dist-test)))
;(defn alex-ledger-func []
;  (println "running alex-ledger-func")
;  (println (render-many all-clips)))
;
;(defn -main
;  "I don't do a whole lot ... yet."
;  [& args]
;  (alex-ledger-func))

(defn -main
  [& args]
  (let [example-facts #{[:faces :boy :girl] [:close :boy :girl] [:close :bear :soccer-ball]}
        image-samples (take 100 (doquery :importance generate-image [example-facts]))
        ;sentence-samples (take 100 (doquery :importance generate-sentence [example-facts]))
        image-sample (first image-samples)
        ;sentence-sample (first sentence-samples)
    ]
    (println (get image-sample :result))
    ;(println (get sentence-sample :result))
    (render-to-file (image-sample :result) "example-facts")))
