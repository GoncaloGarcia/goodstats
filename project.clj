(defproject goodstats "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Mozilla Public License 2.0"
            :url  "none"
            :year 2020
            :key "mpl-2.0"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.1"]
                 [org.clojure/data.xml "0.0.8"]
                 ]
  :plugins [
            [lein-cljfmt "0.6.8"]
            [lein-cloverage "1.1.2"]
            [lein-license "0.1.8"]
            ]
  :main ^:skip-aot goodstats.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
