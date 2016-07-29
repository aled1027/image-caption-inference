(ns image_caption.core
  (:use [image_caption globals images sentences clipart])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:gen-class)
  (:import [robots.Clipart Clipart]))

(defn -main
  [& args]
  (let [example-facts #{[:kicks :boy :girl]}
        image-samples (take 50 (doquery :importance generate-image-from-facts-query [example-facts]))
        images (map #(:result %) image-samples)]
    (render-many-to-files images "output/image")))
