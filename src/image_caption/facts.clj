(ns image_caption.facts
  (:use [anglican core runtime emit])
  (:use [image_caption globals]))

(with-primitive-procedures [concatv]
  (defm simple-fact-prior []
    (let [verbs' (vec (keys verbs))
          num-facts (sample (uniform-discrete 1 5))]
      (repeatedly
       num-facts
       (fn [] (let [verb (sample-from-vector verbs')
                    arity (:arity (get verbs verb))]
                (concatv
                 [verb]
                 (vec (repeatedly arity (fn [] (sample-from-vector nouns)))))))))))


