(ns image_caption.clipart
  (:use [image_caption globals])
  (:import [robots.Clipart Clipart]))

(def renderer (Clipart. image-width image-height)) ; Renderer object

(defn draw-clip [filename x-pos y-pos flip]
  (.addClip renderer filename x-pos y-pos flip))

(defn draw-clips [clips]
  ;; clips = vector of dictionaries of :sprite :x :y :flip
  ;; e.g. [{:sprite :boy :x 0 :y 0 :flip 0} {:sprite :girl :x 200 :y 100 :flip 1}]
  ;; draws the clips onto the clipart
  ;; flip means flip the clipart in around the y-axis
  (mapv (fn [clip]
          (let [filename ((:sprite clip) clip-map)
                x-pos (:x clip)
                y-pos (:y clip)
                flip (:flip clip)]
            (draw-clip filename x-pos y-pos flip)))
        clips))

(defn render [clips]
  ;; render image, return 2D array of image
  (.background renderer)
  (draw-clips clips)
  (mapv #(into [] %) (seq (.getImageArray2D renderer))))

(defn render-many [many-clips]
  ;; render many images, returns a vector of
  ;; 2D array of images
  (mapv (fn [clips] (render clips)) many-clips))

(defn render-to-file [clips filename]
  ;; renders and saves image to filename
  (println "Writing to " filename)
  (.background renderer)
  (draw-clips clips)
  (.save renderer filename))

(defn render-many-to-files [many-clips path]
  (mapv
    (fn [clips i] (render-to-file clips (str path i ".png")))
    many-clips
    (range 0 (count many-clips))))
