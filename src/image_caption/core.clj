(ns image_caption.core
  (:use [image_caption globals images sentences images_renderer images_similarity])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:gen-class))


(defn -main
  [& args]
  (test-image-distance))

  ;(let [example-facts #{[:kicks :bear :soccer-ball] [:kicks :boy :soccer-ball]}
  ;      image-samples (take 10 (doquery :importance generate-image-from-facts-query [example-facts]))
  ;      images (map #(:result %) image-samples)]
  ;  (render-many-to-files images "output/image")
  ;  (println (render (first images)))))
