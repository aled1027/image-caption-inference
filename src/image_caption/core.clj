(ns image_caption.core
  (:use [image_caption globals images sentences images_renderer images_similarity])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit inference])
  (:gen-class))

(defn -main
  [& args]
  (let [image (read-image "input.png")
        samples (take 10 (drop 100
                               (doquery :rmh generate-image 
                                        [image] :numer-of-particles 1000)))]
    (save-many-images (map #(:image (:result %)) samples) "output/image")
    (println "same image      " (image-similarity image image))
    ;(println "different image " (image-similarity (read-image "different.png") image))
    (println "generated image " (image-similarity (:image (:result (first samples))) image))

    (println (map
               #(:facts (:result %))
               samples))
    ))
