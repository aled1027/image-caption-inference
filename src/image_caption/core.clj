(ns image_caption.core
  (:use [image_caption globals images sentences images_renderer images_similarity])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit inference])
  (:gen-class))

(defn -main
  [& args]
  (let [image (read-image "input.png")
        samples (take 10 (doquery :smc generate-image [image] :numer-of-particles 100))
        samples-2 (collect-results samples)]
    (println (first samples-2))))
      

    ;(save-many-images (map #(:image (:result %)) samples) "output/image")
    ;(println (map
    ;           #(image-distance
    ;             (get-greyscale-pixels image)
    ;             (get-greyscale-pixels (:image (:result %))))
    ;          samples))))

;(defn -main
;  [& args]
;  (let [example-facts #{[:kicks :bear :soccer-ball] [:kicks :boy :soccer-ball]}
;        samples (take 10 (doquery generate-image-from-facts-query [example-facts]))]
;    (save-many-images (map #(:result %) samples) "output/image")))
