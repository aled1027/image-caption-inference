(ns image_caption.images_similarity
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals])
  (:require [clojure.core.matrix :as m]))

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
