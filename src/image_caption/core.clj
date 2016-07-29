(ns image_caption.core
  (:use [image_caption globals images sentences clipart])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:require [clojure.core.matrix :as m])
  (:gen-class)
  (:import [robots.Clipart Clipart]))

(defn -main
  [& args]
  (let [example-facts #{[:kicks :boy :girl]}
        image-samples (take 50 (doquery :importance generate-image [example-facts]))
        many-clips (map #(:result %) image-samples)]
    (render-many-to-files many-clips "output/image")))

;(defn dist-test []
;  (let [img1 (render (nth all-clips 0))
;        img2 (render (nth all-clips 1))]
;    (println (image-similarity img1 img2))))
;
;(defn alex-ledger-func []
;  (println "running alex-ledger-func")
;  (dist-test))
;  (println (dist-test)))
