(defn nouns []
            [:boy
            :girl
            :soccer-ball
            :bear]
)

(defn verbs []
            {:close {:arity 2}
             :faces {:arity 2}
             :kicks {:arity 2}}

; facts have structure:
;
; #{
;   [:close :boy :bear]
;   [:kicks :girl :soccer-ball]
; }
;
