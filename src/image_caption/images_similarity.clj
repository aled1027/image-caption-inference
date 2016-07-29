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

(defn image-distance-random-projection [img1 img2]
  (let [r_img1 (m/to-vector (reduce-dim img1))
        r_img2 (m/to-vector (reduce-dim img2))
        dist (m/distance r_img1 r_img2)]
    dist))

(defn scaled-histogram [image]
  (let [hist (.scaled_histogram image 2)]
    (seq hist)))

(defn image-distance-scaled-histogram [img1 img2]
  (let [h_img1 (m/to-vector (scaled-histogram img1))
        h_img2 (m/to-vector (scaled-histogram img2))
        dist (m/distance h_img1 h_img2)
        scaled_dist (/ dist (* image-width image-height))]
    scaled_dist))

(defn blocked-histogram [image]
  (let [hist (.blocked_histogram image 8)]
    (seq hist)))

(defn image-distance-blocked-histogram [img1 img2]
  (let [h_img1 (m/to-vector (blocked-histogram img1))
        h_img2 (m/to-vector (blocked-histogram img2))
        dist (m/distance h_img1 h_img2)
        scaled_dist (/ dist (* image-width image-height))]
    scaled_dist))

(defn image-distance [img1 img2]
  (image-distance-blocked-histogram img1 img2))
