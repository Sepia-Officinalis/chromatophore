(defproject chromatophore "0.1.2"
  :description "CuttleFi.sh Reusable Components for Reagent"

  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/cljc"]

  :dependencies [[garden "1.3.0"]
                 [hiccup "1.0.5"]
                 [markdown-clj "0.9.85"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [prismatic/schema "1.0.4"]
                 [reagent "0.6.0-alpha"]]

  :clean-targets ^{:protect false} [:target-path
                                    "dev-resources/public/js/compiled/"
                                    "dev-resources/public/css/compiled/"
                                    "out/"]

  :profiles
  {:dev
   {:plugins      [[com.jakemccrary/lein-test-refresh "0.12.0"]
                   [lein-cljsbuild "1.1.2"
                    :exclusions
                    [[org.apache.commons/commons-compress]
                     [org.clojure/clojure]]]
                   [lein-npm "0.6.2"]
                   [lein-figwheel "0.5.0-6"]
                   [lein-garden "0.2.6"]
                   [lein-doo "0.1.6"]
                   [lein-pdo "0.1.1"]
                   [lein-shell "0.5.0"]]

    :resource-paths ["dev-resources/"]

    :dependencies [[devcards "0.2.1-6"]
                   [org.apache.commons/commons-compress "1.4"]
                   [javax.servlet/servlet-api "2.5"]]

    :garden       {:builds
                   [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   chromatophore.css.screen/style
                     :compiler     {:output-to     "dev-resources/public/css/compiled/screen.css"
                                    :pretty-print? true}}]}

    ;; Use NPM to get slimerjs and phantomjs
    :npm          {:dependencies [[slimerjs "0.9.6"
                                   phantomjs-prebuilt "2.1.5"
                                   karma-cljs-test "0.1.0"
                                   karma-firefox-launcher "0.1.7"
                                   karma-chrome-launcher "0.2.2"
                                   karma "0.13.22"]]}

    :doo          {:paths {:slimer    "./node_modules/.bin/slimerjs"
                           :phantomjs "./node_modules/.bin/phantomjs"}
                   :alias {:browsers [:chrome :firefox]
                           :all      [:browsers :headless]}}

    :cljsbuild    {:builds [{:id           "devcards"
                             :source-paths ["src/cljs" "src/cljc" "test/cljs" "test/cljc"]
                             :figwheel     {:devcards true}
                             :compiler     {:main                 "chromatophore.devcards.core"
                                            :asset-path           "js/compiled/devcards_out"
                                            :output-to            "dev-resources/public/js/compiled/chromatophore_devcards.js"
                                            :output-dir           "dev-resources/public/js/compiled/devcards_out"
                                            :source-map-timestamp true}}

                            {:id           "test"
                             :source-paths ["src/cljs" "src/cljc" "test/cljc" "test/cljs" "test/doo"]
                             :compiler     {:output-to     "target/js/compiled/testable.js"
                                            :main          "chromatophore.doo.runner"
                                            :optimizations :none}}

                            {:id           "test-advanced"
                             :source-paths ["src/cljs" "src/cljc" "test/cljs" "test/cljc" "test/doo"]
                             :compiler     {:output-to     "target/js/compiled/testable.min.js"
                                            :main          "chromatophore.doo.runner"
                                            :optimizations :advanced
                                            :pretty-print  false}}]}

    :figwheel     {:css-dirs ["resources/public/css"]}
    :aliases      {"garden-and-devcards" ["pdo"
                                          "garden" "auto,"
                                          "test-refresh,"
                                          "figwheel,"]
                   "devcards"            ["do"
                                          "clean,"
                                          "garden" "once,"
                                          "test,"
                                          "garden-and-devcards,"]
                   "test-auto"           ["do"
                                          "clean,"
                                          "npm" "install,"
                                          "doo" "phantom" "test" "auto,"]
                   "auto-test"           ["test-auto"]
                   "test-advanced"       ["do"
                                          "clean,"
                                          "npm" "install,"
                                          "garden" "once,"
                                          "test,"
                                          "doo" "all" "test" "once,"
                                          "doo" "all" "test-advanced" "once,"]
                   "advanced-test"       ["test-advanced"]
                   "deep-clean"          ["do"
                                          "shell" "rm" "-rf" "figwheel_server.log" "node_modules,"
                                          "clean"]}}})
