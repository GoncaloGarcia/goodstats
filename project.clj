(defproject goodstats "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.1"]
                 [org.clojure/data.xml "0.0.8"]
                 ]
  :plugins [
            [lein-cljfmt "0.6.8"]
            [lein-cloverage "1.1.2"]
            ]
  :main ^:skip-aot goodstats.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
