(ns image-caption.core
  (:gen-class)
  (:import [robots.Clipart Clipart]))

;; other imports that captcha has. May need later
;(:refer-clojure :exclude [rand rand-nth rand-int name read])
;(:require [clojure.string :as str]
;          [clojure.core.matrix :as m]
;          [clojure.java.io :as io])
;(:use clj-hdf5.core
;      [anglican runtime emit core inference])
;(:import (ch.systemsx.cisd.hdf5 HDF5Factory IHDF5SimpleReader
;                                IHDF5SimpleWriter HDF5FactoryProvider
;                                HDF5DataClass HDF5StorageLayout)

(def image-width 400) ; Width of CAPTCHA
(def image-height 400) ; Height of CAPTCHA
(def clipart (Clipart. image-width image-height)) ; Renderer object
(def x-offsets [100 100 100])
(def y-offsets [40 40 30])
(def letters (char-array "B"))
(def clips [])

(def clip-map {
               :boy "resources/clipart_pngs/hb0_7s.png" 
               :girl "resources/clipart_pngs/hb1_7s.png" 
               :soccer-ball "resources/clipart_pngs/t_4s.png" 
               :bear "resources/clipart_pngs/a_0s.png" 
               })

(def clip-filename (:boy clip-map))
(def x-pos 0)
(def y-pos 0)

(defn old-render [xs ys letters & {:keys [mode] :or {mode Clipart/RELATIVE}}]
  ;; returns a 2d array of the image
  (.background clipart)
  (.text clipart (char-array letters) (int-array xs) (int-array ys) mode)
  (.blurGaussian clipart 2 2.0)
  (mapv #(into [] %) (seq (.getImageArray2D clipart))))

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
  (let [clips [{:sprite :boy :x 0 :y 0}
               {:sprite :girl :x 100 :y 100}
               {:sprite :soccer-ball :x 200 :y 200}
               {:sprite :bear :x 50 :y 300}]]
    (.background clipart)
    (draw-clips clips)
    (.save clipart filename)))

(defn alex-ledger-func []
  (println "running alex-ledger-func")
  (render-to-file clips "file.png"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (alex-ledger-func))
