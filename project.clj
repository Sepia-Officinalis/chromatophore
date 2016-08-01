(defproject chromatophore "0.1.13"
  :description "CuttleFi.sh Reusable Components for Reagent"

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :test-paths ["test/cljc" "test/cljs"]

  :dependencies [[garden                    "1.3.2"]
                 [hiccup                    "1.0.5"]
                 [markdown-clj              "0.9.89"]
                 [org.clojure/clojure       "1.9.0-alpha10"]
                 [org.clojure/clojurescript "1.9.93"]
                 [reagent                   "0.6.0-rc"
                  :provided [cljsjs/react
                             cljsjs/react-dom
                             cljsjs/react-dom-server]]]

  :clean-targets ^{:protect false} [:target-path
                                    "dev-resources/public/js/compiled/"
                                    "dev-resources/public/css/compiled/"
                                    "out/"]

  :profiles {:dev [~(if (= (System/getProperty "os.name") "Mac OS X")
                      :profiles/osx
                      :profiles/linux)
                   :profiles/dev]}

  :cljsbuild
  {:builds
   [{:id "devcards"
     :source-paths ["src/cljs" "src/cljc" "test/cljs" "test/cljc"]
     :figwheel {:devcards true}
     :compiler
     {:main "chromatophore.devcards.core"
      :asset-path "js/compiled/devcards_out"
      :output-to "dev-resources/public/js/compiled/chromatophore_devcards.js"
      :output-dir "dev-resources/public/js/compiled/devcards_out"
      :source-map-timestamp true}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
