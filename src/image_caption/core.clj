(ns image_caption.core
  (:use [image_caption globals images sentences images_renderer images_similarity])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit inference])
  (:gen-class))

(defn -main
  [& args]
  (let [image (read-image "input.png")
        samples (take 10 (drop 100
                    (doquery :lmh generate-image [image] :numer-of-particles 1000)))]
    (save-many-images (map #(:image (:result %)) samples) "output/image")
    (println "same image      " (image-distance image image))
    (println "different image " (image-distance (read-image "different.png") image))
    (println "generated image " (image-distance (:image (:result (first samples))) image))
    ;(println (map
    ;          #(:facts (:result %))
    ;          samples))
))

;(defn -main
;  [& args]
;  (let [example-facts #{[:close :bear :soccer-ball]}
;        samples (take 10 (doquery :importance generate-image-from-facts-query [example-facts]))]
;    (save-many-images (map #(:result %) samples) "output/image")))

;(defn -main
;  [& args]
;  (println (histogram (read-image "input.png"))))
