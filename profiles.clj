{:profiles/dev
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

  :dependencies   [[cljsjs/react              "15.3.0-0"]
                   [cljsjs/react-dom          "15.3.0-0"]
                   [cljsjs/react-dom-server   "15.3.0-0"]
                   [devcards "0.2.1-7"
                    :exclusions [cljs/react]]
                   [org.apache.commons/commons-compress "1.12"]
                   [doo "0.1.7"]
                   [javax.servlet/servlet-api "2.5"]]

  ;; Use NPM to get slimerjs and phantomjs
  :npm            {:dependencies [[slimerjs "0.906.2"
                                   phantomjs-prebuilt "2.1.9"
                                   karma-cljs-test "0.1.0"
                                   karma-firefox-launcher "0.1.7"
                                   karma-chrome-launcher "0.2.2"
                                   karma "0.13.22"]]}

  :garden         {:builds
                   [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   chromatophore.css.screen/style
                     :compiler     {:output-to     "dev-resources/public/css/compiled/screen.css"
                                    :pretty-print? true}}]}

  :cljsbuild
  {:builds
   [{:id           "test"
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

  :doo {:paths {:slimer    "./node_modules/.bin/slimerjs"
                :phantomjs "./node_modules/.bin/phantomjs"
                :karma     "./node_modules/.bin/karma"}
        :alias {:headless [:slimer :phantom :nashorn]
                :all      [:browsers :headless]}}

  :aliases
  {"garden-and-devcards" ["pdo"
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
                          "clean"]}}

 :profiles/linux {:doo {:alias {:browsers [:chrome :firefox]}}}
 :profiles/osx {:doo {:alias {:browsers [:chrome :firefox :safari]}}
                :npm {:dependencies [[karma-safari-launcher "1.0.0"]]}}}
