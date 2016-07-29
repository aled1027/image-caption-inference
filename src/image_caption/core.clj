(ns image_caption.core
  (:use [image_caption globals images sentences clipart])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:require [clojure.core.matrix :as m])
  (:gen-class)
  (:import [robots.Clipart Clipart]))

(def clipart (Clipart. image-width image-height)) ; Renderer object

(defn vector-dot [xs ys]
  ;; computes the dot product between two vectors
  map (fn [x y] (* x y)) xs ys)

(defn normalize [xs]
  ;; normalizes a vector
  (let [squared (vector-dot xs xs)
        summed (reduce + squared)
        square-root (sqrt summed)
        final (map (fn [x] (/ x square-root)) xs)]
    final))

(defn image-euclidean-distance [img1 img2] 
  ;; computes absolute value of euclidean distance between two images
  ;; i.e. a value between 0 and 1
  (let [img1 (m/to-vector img1)
        img2 (m/to-vector img2)]
    (m/distance img1 img2)))

(defn get-random-projection-matrix [n m]
  (m/matrix 
    (vec 
      (take n (repeatedly
                (fn [] 
                  (vec (take m (repeatedly #(sample* (uniform-discrete -1 2)))))))))))

(defn reduce-dim [im]
  (let [mat (m/matrix im)
        proj1 (get-random-projection-matrix image-reduction-constant image-height)
        proj2 (get-random-projection-matrix image-width image-reduction-constant)
        times (m/mmul proj1 (m/mmul mat proj2))]
    (m/normalise (m/to-vector times))))

(defn image-similarity [img1 img2] 
  (image-euclidean-distance 
    (reduce-dim img1)
    (reduce-dim img2)))

(defn dist-test []
  (let [img1 (render (nth all-clips 0))
        img2 (render (nth all-clips 1))]
    (println (image-similarity img1 img2))))

(defn alex-ledger-func []
  (println "running alex-ledger-func")
  (dist-test))
;(println (dist-test)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (alex-ledger-func))

  ;(let [example-facts #{[:kicks :boy :girl]}
  ;      image-samples (take 100 (doquery :importance generate-image [example-facts]))
  ;      ;sentence-samples (take 100 (doquery :importance generate-sentence [example-facts]))
  ;      image-sample (first image-samples)
  ;      ;sentence-sample (first sentence-samples)
  ;  ]
  ;  (println (get image-sample :result))
  ;  ;(println (get sentence-sample :result))
  ;  (render-to-file (image-sample :result) "example-facts")))
