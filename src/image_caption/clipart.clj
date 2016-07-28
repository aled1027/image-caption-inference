(ns image-caption.clipart
  (:use [image_caption globals])
  (:gen-class)
  (:import [robots.Clipart Clipart]))

(def clipart (Clipart. image-width image-height)) ; Renderer object

(defn draw-clip [filename x-pos y-pos]
  (.addClip clipart filename x-pos y-pos))

(defn draw-clips [clips]
  ;; clips = vector of dictionaries of :sprite :x :y :flip
  ;; e.g. [{:sprite :boy :x 0 :y 0 :flip 0} {:sprite :girl :x 200 :y 100 :flip 1}]
  ;; draws the clips onto the clipart
  ;; flip means flip the clipart in around the y-axis
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
