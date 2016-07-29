(ns image_caption.core
  (:use [image_caption globals images sentences images_renderer images_similarity])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:gen-class))

;(defn -main
;  [& args]
;  (let [image (read-image "input.png")
;        samples (take 10 (doquery :importance generate-image [image]))]
;    (println (first samples))))
;    ;(save-to-file (:image (:predicts (first samples))))))

(defn -main
  [& args]
  (let [example-facts #{[:kicks :bear :soccer-ball] [:kicks :boy :soccer-ball]}
        samples (take 10 (doquery :importance generate-image-from-facts-query [example-facts]))]
    (save-many-images (map #(render (:result %)) samples) "output/image")))
