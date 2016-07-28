(ns image_caption.globals)

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

; width and height of images
(def image-dim
  [200 100]
)

;; Globals for rendering image
(def image-width 400) ; Width of CAPTCHA
(def image-height 400) ; Height of CAPTCHA
(def clip-map {:boy "resources/clipart_pngs/hb0_7s.png" 
               :girl "resources/clipart_pngs/hb1_7s.png" 
               :soccer-ball "resources/clipart_pngs/t_4s.png" 
               :bear "resources/clipart_pngs/a_0s.png"})

;; for testing
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


