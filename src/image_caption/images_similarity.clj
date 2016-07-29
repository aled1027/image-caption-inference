(ns image_caption.images_similarity
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals images_renderer])
  (:require [clojure.core.matrix :as m]))

(defn get-random-projection-matrix [m n]
  "Generates a random m by n with entires -1, 0 or 1"
  (m/matrix
    (vec
      (take m (repeatedly
                (fn []
                  (vec (take n (repeatedly #(sample* (uniform-discrete -1 2)))))))))))

(def proj1
  (get-random-projection-matrix image-reduction-constant image-height))

(def proj2
  (get-random-projection-matrix image-width image-reduction-constant))

(defn reduce-dim [im]
  (let [mat (m/matrix im)
        times (m/mmul proj1 (m/mmul mat proj2))]
    (m/normalise (m/to-vector times))))

(defn image-distance [img1 img2]
  (let [r_img1 (m/to-vector (reduce-dim img1))
        r_img2 (m/to-vector (reduce-dim img2))
        dist (m/distance r_img1 r_img2)]
    dist))

(defn test-image-distance []
  (let [img1 (read-image "resources/examples/example1.png")]
        (println (histogram img1))))




    ;    img2 (read-image "resources/examples/example8.png")
    ;    sim (image-distance img1 img2)]
    ;(println sim)))

