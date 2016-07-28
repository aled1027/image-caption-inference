(ns image-caption.core
  (:use [image_caption globals images])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:gen-class)

  (:import [robots.Clipart Clipart]))


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

;; saving to disk
(defn render-to-file [clips filename]
  (.background clipart)
  (draw-clips clips)
  (.save clipart filename))

(defn render [clips]
  (.background clipart)
  (draw-clips clips)
  (mapv #(into [] %) (seq (.getImageArray2D clipart))))

(defn render-many [many-clips]
  (mapv (fn [clips] (render clips)) many-clips))

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
  (let [example-facts #{[:close :boy :girl] [:kicks :girl :boy]}
        samples (take 10 (doquery :importance generate-image [example-facts]))]
    (println (first samples))
    (render-to-file (get (first samples) :result) "example-facts")))
