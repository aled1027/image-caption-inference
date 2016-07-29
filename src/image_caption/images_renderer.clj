(ns image_caption.images_renderer
  (:use [image_caption globals])
  (:import [image_caption Image]))

(defn render-sprite [image sprite]
  "Draw a sprite into the given image"
  (.renderSprite
    image
    ((:sprite sprite) sprite-map)
    (:x sprite)
    (:y sprite)
    (:flip sprite)
    (:scale sprite)))

(defn render [sprites]
  "Render image from a list of sprites. Returns an image object."
  (let [image (Image. image-width image-height)]
    (mapv #(render-sprite image %) sprites)
    image))

(defn save-image [image filename]
  "Write an image to a file."
  (println "Writing image to " filename)
  (.save image filename))

(defn save-many-images [images path-prefix]
  "Save multiple images to file, with the given filename prefix"
  (mapv
    (fn [image i] (save-image image (str path-prefix i ".png")))
    images
    (range 0 (count images))))

(defn read-image [filename]
  "Read an image from disk. Returns an image object."
  (let [image (Image. image-width image-height)]
    (.load image filename)
    image))

(defn get-greyscale-pixels [image]
  (mapv #(into [] %) (seq (.getGrayscalePixels image))))
