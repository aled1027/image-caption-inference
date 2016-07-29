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
(def sprite-map {:background "resources/clipart_pngs/background.png"
                 :boy "resources/clipart_pngs/hb0_7s.png"
                 :boy-kicking "resources/clipart_pngs/hb0_13s.png"
                 :girl "resources/clipart_pngs/hb1_7s.png"
                 :girl-kicking "resources/clipart_pngs/hb1_10s.png"
                 :soccer-ball "resources/clipart_pngs/t_4s.png"
                 :soccer-ball-kicking "resources/clipart_pngs/t_4s_kicking.png"
                 :bear "resources/clipart_pngs/a_0s.png"
                 :bear-kicking "resources/clipart_pngs/a_0s_kicking.png"})

(defm sample-from-vector [vector]
  (if (not= 0 (count vector))
    (let [i (sample (uniform-discrete 0 (count vector)))]
      (get vector i))
    nil))

(defn sample*-from-vector [vector]
  (if (not= 0 (count vector))
    (let [i (sample* (uniform-discrete 0 (count vector)))]
      (get vector i))
    nil))

(defn concatv [& args]
  (into [] (apply concat args)))

(defdist mixture-distribution [p dist-a dist-b] []
  (sample* [this]
           (if (sample* (flip p))
             (sample* dist-a)
             (sample* dist-b)))
  (observe* [this value]
            (log-sum-exp (+ (log p) (observe* dist-a value))
                         (+ (log (- 1 p)) (observe* dist-b value)))))


