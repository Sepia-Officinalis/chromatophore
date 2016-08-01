(defproject chromatophore "0.1.10"
  :description "CuttleFi.sh Reusable Components for Reagent"

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :test-paths ["test/cljc" "test/cljs"]

  :dependencies [[cljsjs/react              "15.3.0-0"]
                 [cljsjs/react-dom          "15.3.0-0"]
                 [cljsjs/react-dom-server   "15.3.0-0"]
                 [garden                    "1.3.2"]
                 [hiccup                    "1.0.5"]
                 [markdown-clj              "0.9.89"]
                 [org.clojure/clojure       "1.9.0-alpha10"]
                 [org.clojure/clojurescript "1.9.93"]
                 [reagent                   "0.6.0-rc"
                  :exclusions [cljsjs/react
                               cljsjs/react-dom
                               cljsjs/react-dom-server]]]

  :clean-targets ^{:protect false} [:target-path
                                    "dev-resources/public/js/compiled/"
                                    "dev-resources/public/css/compiled/"
                                    "out/"]

  :profiles
  {:dev
   {:plugins        [[lein-cljsbuild "1.1.3"
                      :exclusions
                      [[org.apache.commons/commons-compress]
                       [org.clojure/clojure]]]
                     [lein-npm "0.6.2"]
                     [lein-figwheel "0.5.4-7"]
                     [lein-garden "0.2.6"]
                     [lein-doo "0.1.7"]
                     [lein-pdo "0.1.1"]
                     [lein-shell "0.5.0"]]

    :resource-paths ["dev-resources/"]

    :dependencies   [[devcards "0.2.1-7"
                      :exclusions [cljs/react]]
                     [org.apache.commons/commons-compress "1.12"]
                     [doo "0.1.7"]
                     [javax.servlet/servlet-api "2.5"]]}}

  :garden         {:builds
                   [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   chromatophore.css.screen/style
                     :compiler     {:output-to     "dev-resources/public/css/compiled/screen.css"
                                    :pretty-print? true}}]}

  ;; Use NPM to get slimerjs and phantomjs
  :npm            {:dependencies [~(let [pkgs ['slimerjs "0.906.2"
                                               'phantomjs-prebuilt "2.1.7"
                                               'karma-cljs-test "0.1.0"
                                               'karma-firefox-launcher "0.1.7"
                                               'karma-chrome-launcher "0.2.2"
                                               'karma "0.13.22"]]
                                     (if (= (System/getProperty "os.name") "Mac OS X")
                                       (conj pkgs 'karma-safari-launcher "1.0.0")
                                       pkgs))]}

  :doo            {:paths {:slimer    "./node_modules/.bin/slimerjs"
                           :phantomjs "./node_modules/.bin/phantomjs"
                           :karma     "./node_modules/.bin/karma"}
                   :alias {:browsers ~(if (= (System/getProperty "os.name") "Mac OS X")
                                        [:chrome :firefox :safari]
                                        [:chrome :firefox])
                           :headless [:slimer :phantom :nashorn]
                           :all      [:browsers :headless]}}

  :cljsbuild     {:builds [{:id           "devcards"
                            :source-paths ["src/cljs" "src/cljc" "test/cljs" "test/cljc"]
                            :figwheel     {:devcards true}
                            :compiler     {:main                 "chromatophore.devcards.core"
                                           :asset-path           "js/compiled/devcards_out"
                                           :output-to            "dev-resources/public/js/compiled/chromatophore_devcards.js"
                                           :output-dir           "dev-resources/public/js/compiled/devcards_out"
                                           :source-map-timestamp true}}

                           {:id           "test"
                            :source-paths ["src/cljs" "src/cljc" "test/cljc" "test/cljs"]
                            :compiler     {:output-to     "target/js/compiled/testable.js"
                                           :main          "chromatophore.doo.runner"
                                           :optimizations :whitespace}}

                           {:id           "test-advanced"
                            :source-paths ["src/cljs" "src/cljc" "test/cljs" "test/cljc"]
                            :compiler     {:output-to     "target/js/compiled/testable.min.js"
                                           :main          "chromatophore.doo.runner"
                                           :optimizations :advanced
                                           :pretty-print  false}}]}

  :figwheel      {:css-dirs ["resources/public/css"]}
  :aliases       {"garden-and-devcards" ["pdo"
                                         "garden" "auto,"
                                         "figwheel,"]
                  "devcards"            ["do"
                                         "clean,"
                                         "garden" "once,"
                                         "test,"
                                         "garden-and-devcards,"]
                  "test-auto"           ["do"
                                         "clean,"
                                         "npm" "install,"
                                         "doo" "slimer" "test" "auto,"]
                  "test-advanced"       ["do"
                                         "clean,"
                                         "npm" "install,"
                                         "garden" "once,"
                                         "test,"
                                         "doo" "all" "test" "once,"
                                         "doo" "all" "test-advanced" "once,"]
                  "deep-clean"          ["do"
                                         "shell" "rm" "-rf" "figwheel_server.log" "node_modules,"
                                         "clean"]})
