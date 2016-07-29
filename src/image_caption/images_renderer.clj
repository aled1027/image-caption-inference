(ns image_caption.images_renderer
  (:use [image_caption globals])
  (:import [image_caption Image]))

(def renderer (Image. image-width image-height))

(defn render-sprite [sprite]
  "Draw a sprite into the current renderer"
  (.renderSprite
    renderer
    ((:sprite sprite) sprite-map)
    (:x sprite)
    (:y sprite)
    (:flip sprite)
    (:scale sprite)))

(defn render [sprites]
  "Render image from a list of sprites. Returns a 2D array of greyscale pixel values."
  (mapv render-sprite sprites)
  (mapv #(into [] %) (seq (.getGrayscalePixels renderer))))

(defn render-to-file [sprites filename]
  "Render and save an image to a file. Renders an RGB image."
  (println "Writing image to " filename)
  (mapv render-sprite sprites)
  (.save renderer filename))

(defn render-many-to-files [many-sprites path]
  "Render and save multiple images to file, with the given filename prefix"
  (mapv
    (fn [sprites i] (render-to-file sprites (str path i ".png")))
    many-sprites
    (range 0 (count many-sprites))))

(defn read-image [filename]
  "Read an image from disk. Returns a 2D array of greyscale pixel values.
  If running from project root, use (read-image resources/examples/example0.png"
  (.load renderer filename)
  (mapv #(into [] %) (seq (.getGrayscalePixels renderer))))

(defn histogram []
  (let [scale 0.5
        java-hist (.histogram renderer (* scale image-width) (* scale image-height) scale scale)
        hist (seq java-hist)]
    hist))

