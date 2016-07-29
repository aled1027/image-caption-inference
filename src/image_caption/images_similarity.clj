(ns image_caption.images_similarity
  (:use [clojure string])
  (:use [anglican core runtime emit])
  (:use [image_caption globals images_renderer])
  (:require [clojure.core.matrix :as m]))

(def block-size 32)

(defn scaled-histogram [image]
  (let [hist (.scaled_histogram image 2)]
    (seq hist)))

(defn image-distance-scaled-histogram [img1 img2]
  (let [h_img1 (m/to-vector (scaled-histogram img1))
        h_img2 (m/to-vector (scaled-histogram img2))]
    (m/distance h_img1 h_img2)))

(defn blocked-histogram-grayscale [image]
  (let [hist (.blocked_histogram_grayscale image block-size)]
    (seq hist)))

(defn blocked-histogram [image]
  (let [hist (.blocked_histogram image block-size)]
    (seq hist)))

(defn image-distance-blocked-histogram [img1 img2]
  (let [h_img1 (m/to-vector (blocked-histogram img1))
        h_img2 (m/to-vector (blocked-histogram img2))]
    (m/distance h_img1 h_img2)))

(defn image-similarity [img1 img2]
  (- (image-distance-blocked-histogram img1 img2)))
