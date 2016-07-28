(ns image-caption.core
  (:gen-class)
  (:import [robots.Clipart Clipart]))

(def image-width 400) ; Width of CAPTCHA
(def image-height 400) ; Height of CAPTCHA
(def clipart (Clipart. image-width image-height)) ; Renderer object
(def x-offsets [100 100 100])
(def y-offsets [40 40 30])
(def letters (char-array "B"))
(def clips [])

(def clip-map {
               :boy "resources/clipart_pngs/hb0_7s.png" 
               :girl "resources/clipart_pngs/hb1_7s.png" 
               :soccer-ball "resources/clipart_pngs/t_4s.png" 
               :bear "resources/clipart_pngs/a_0s.png" 
               })

(def clip-filename (:boy clip-map))
(def x-pos 0)
(def y-pos 0)

(defn old-render [xs ys letters & {:keys [mode] :or {mode Clipart/RELATIVE}}]
  ;; returns a 2d array of the image
  (.background clipart)
  (.text clipart (char-array letters) (int-array xs) (int-array ys) mode)
  (.blurGaussian clipart 2 2.0)
  (mapv #(into [] %) (seq (.getImageArray2D clipart))))

(defn draw-clip [filename x-pos y-pos]
  (.addClip clipart filename x-pos y-pos))

(defn draw-clips [clips]
  ;; clips = vector of dictionaries of :sprite :x :y
  ;; e.g. [{:sprite :boy :x 0 :y 0} {:sprite :girl :x 200 :y 100}]
  ;; draws the clips onto the clipart
  (mapv (fn [clip] 
         (let [filename ((:sprite clip) clip-map)
               x-pos (:x clip)
               y-pos (:y clip)]
           (draw-clip filename x-pos y-pos)))
       clips))

(defn render-to-file [clips filename]
    (.background clipart)
    (draw-clips clips)
    (.save clipart filename))

(def all-clips [[{:sprite :boy :x 0 :y 30}
                {:sprite :girl :x 109 :y 110}
                {:sprite :soccer-ball :x 210 :y 200}
                {:sprite :bear :x 51 :y 320}]

                [{:sprite :boy :x 0 :y 10}
                {:sprite :girl :x 100 :y 180}
                {:sprite :soccer-ball :x 230 :y 200}
                {:sprite :bear :x 200 :y 320}]

                [{:sprite :boy :x 0 :y 20}
                {:sprite :girl :x 100 :y 200}
                {:sprite :soccer-ball :x 100 :y 200}
                {:sprite :bear :x 50 :y 200}]])

(defn render-many [n]
    (loop [i 0
           clips (first all-clips)]
      (render-to-file clips (clojure.string/join ["file" (str i) ".png"]))
      (if (< i n)
        (recur (+ i 1) (nth all-clips i))
        true)))


(defn alex-ledger-func []
  (println "running alex-ledger-func")
  (render-many 3))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (alex-ledger-func))
