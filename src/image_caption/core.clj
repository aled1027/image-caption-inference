(ns image_caption.core
  (:use [image_caption globals images sentences clipart])
  (:use [anglican.core :exclude [-main]])
  (:use [anglican runtime emit])
  (:require [clojure.core.matrix :as m])
  (:gen-class)
  (:import [robots.Clipart Clipart]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [example-facts #{[:kicks :boy :girl]}
        image-samples (take 10 (doquery :importance generate-image [example-facts]))]
    (render-to-files (map #(:result %) image-samples) "image-sample")))

;(defn dist-test []
;  (let [img1 (render (nth all-clips 0))
;        img2 (render (nth all-clips 1))]
;    (println (image-similarity img1 img2))))
;
;(defn alex-ledger-func []
;  (println "running alex-ledger-func")
;  (dist-test))
;  (println (dist-test)))
