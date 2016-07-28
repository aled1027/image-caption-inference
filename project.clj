(defproject image-caption "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-gorilla "0.3.4"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [anglican "1.0.0"]
                 [org.nfrac/cljbox2d.testbed "0.5.0"]
                 [org.nfrac/cljbox2d "0.5.0"]
                 [org.clojure/data.priority-map "0.0.7"]
                 [net.mikera/core.matrix "0.52.2"]
                 [net.mikera/vectorz-clj "0.44.1"]
                 [net.polyc0l0r/clj-hdf5 "0.2.2-SNAPSHOT"]]
  :java-source-paths ["src/ox_captcha", "src/clipart"]
  :main ^:skip-aot examples.core
  ;;:main ^:skip-aot image-caption.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
