(defproject goodstats "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Mozilla Public License 2.0"
            :url  "none"
            :year 2020
            :key "mpl-2.0"}
  ;; CLJ AND CLJS source code paths
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"]
                 [clj-http "3.10.1"]
                 [org.clojure/data.xml "0.0.8"]
                 ]
  :plugins [
            [lein-cljfmt "0.6.8"]
            [lein-cloverage "1.1.2"]
            [lein-license "0.1.8"]
            [lein-cljsbuild "1.1.8"]
            ]
  ;; cljsbuild options configuration
  :cljsbuild {:builds
              [{;; CLJS source code path
                :source-paths ["src/cljs"]

                ;; Google Closure (CLS) options configuration
                :compiler {;; CLS generated JS script filename
                           :output-to "resources/public/js/modern.js"

                           ;; minimal JS optimization directive
                           :optimizations :whitespace

                           ;; generated JS code prettyfication
                           :pretty-print true}}]}
  ;; to clean JS files generated during the build
  :clean-targets ^{:protect false} [:target-path "resources/public/js/"]
  :main ^:skip-aot goodstats.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
