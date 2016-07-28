(ns image-caption.core
  (:use [anglican.core :exclude [-main]])
  (:use [image_caption globals])
  (:use [anglican runtime emit])
  (:gen-class)
  (:import [robots.Clipart Clipart])
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [clojure.java.io :as io]))

(def clipart (Clipart. image-width image-height)) ; Renderer object

(defn draw-clip [filename x-pos y-pos]
  (.addClip clipart filename x-pos y-pos))

(defn draw-clips [clips]
  ;; clips = vector of dictionaries of :sprite :x :y
  ;; e.g. [{:sprite :boy :x 0 :y 0} {:sprite :girl :x 200 :y 100}]
  ;; draws the clips onto the clipart
  (mapv (fn [clip] 
          (let [filename ((:sprite clip) clip-map)
                x-pos (:x clip)
                y-pos (:y clip)]
            (draw-clip filename x-pos y-pos)))
        clips))

(defn render-to-file [clips filename]
  ;; renders and saves image to filename
  (.background clipart)
  (draw-clips clips)
  (.save clipart filename))

(defn render [clips]
  ;; render image, return 2D array of image
  (.background clipart)
  (draw-clips clips)
  (mapv #(into [] %) (seq (.getImageArray2D clipart))))

(defn render-many [many-clips]
  ;; render many images, returns a vector of
  ;; 2D array of images
  (mapv (fn [clips] (render clips)) many-clips))

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

(defn alex-ledger-func []
  (println "running alex-ledger-func")
  (println (dist-test)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (alex-ledger-func))
