(ns image_caption.globals
  (:use [anglican core runtime emit])
  (:use [image_caption globals]))

(def nouns
  [:boy
   :girl
   :soccer-ball
   :bear])

(def verbs
  {:close {:arity 2}
   :faces {:arity 2}
   :kicks {:arity 2}})

; facts have structure:
;
; #{
;   [:close :boy :bear]
;   [:kicks :girl :soccer-ball]
; }
;

;; Globals for rendering image
(def image-reduction-constant 50)
(def image-width 1024) ; Width of CAPTCHA
(def image-height 768) ; Height of CAPTCHA
(def clip-map {:background "resources/clipart_pngs/background.png"
               :boy "resources/clipart_pngs/hb0_7s.png"
               :boy-kicking "resources/clipart_pngs/hb0_13s.png"
               :girl "resources/clipart_pngs/hb1_7s.png"
               :girl-kicking "resources/clipart_pngs/hb1_10s.png"
               :soccer-ball "resources/clipart_pngs/t_4s.png"
               :soccer-ball-kicking "resources/clipart_pngs/t_4s_kicking.png"
               :bear "resources/clipart_pngs/a_0s.png"
               :bear-kicking "resources/clipart_pngs/a_0s_kicking.png"})

;; for testing
(def all-clips [[{:sprite :boy :x 0 :y 30 :flip 0}
                 {:sprite :girl :x 109 :y 110 :flip 0}
                 {:sprite :soccer-ball :x 210 :y 200 :flip 0}
                 {:sprite :bear :x 51 :y 320 :flip 0}]

                [{:sprite :boy :x 100 :y 100 :flip 0}
                 {:sprite :girl :x 0 :y 0 :flip 1}
                 {:sprite :soccer-ball :x 100 :y 200 :flip 0}
                 {:sprite :bear :x 50 :y 50 :flip 1}]])

(defm sample-from-vector [vector]
  (if (not= 0 (count vector))
    (let [i (sample (uniform-discrete 0 (count vector)))]
      (get vector i))
    nil))

(defn concatv [& args]
  (into [] (apply concat args)))

