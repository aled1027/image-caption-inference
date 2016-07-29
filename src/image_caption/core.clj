(ns image_caption.core
  (:use [image_caption globals images sentences images_renderer images_similarity])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit inference])
  (:gen-class))

(defn -main
  [& args]
  (let [image (read-image "input.png")
        samples (take 10 (drop 1000 (doquery :rmh generate-image [image])))]
    (save-many-images (map #(:image (:result %)) samples) "output/image")
    (println "same image      " (image-similarity image image))
    (println "different image " (image-similarity (read-image "different.png") image))
    (println "generated image " (image-similarity (:image (:result (first samples))) image))

    (println (map
               #(:facts (:result %))
               samples))
    ))

;(defn -main
;  [& args]
;  (let [facts #{[:kicks :girl :boy]}
;        sample (first (take 1 (doquery :importance generate-image-from-facts-query [facts])))]
;    (save-image (:result sample) "output.png")))