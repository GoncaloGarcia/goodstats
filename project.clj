(defproject goodstats "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Mozilla Public License 2.0"
            :url  "none"
            :year 2020
            :key  "mpl-2.0"}
  ;; CLJ AND CLJS source code paths
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [hickory "0.7.1"]
                 ]
  :plugins [
            [lein-cljfmt "0.6.8"]
            [lein-cloverage "1.1.2"]
            [lein-license "0.1.8"]
            [lein-cljsbuild "1.1.8"]
            [lein-ring "0.12.5"]
            ]


  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "hello-world.test-runner"]}

  :profiles {:dev  {:dependencies [
                                   [clj-http "3.10.1"]
                                   [org.clojure/data.xml "0.0.8"]
                                   [compojure "1.6.1"]
                                   [cljs-ajax "0.8.0"]
                                   [cheshire "5.10.0"]
                                   [reagent "1.0.0-alpha2"]
                                   [com.bhauman/rebel-readline-cljs "0.1.4"]
                                   [clj-time "0.15.2"]
                                   [metosin/reitit-frontend "0.5.5"]
                                   [clj-oauth "1.5.5"]
                                   [cljsjs/recharts "1.6.2-0"]
                                   [com.bhauman/figwheel-main "0.2.11"]
                                   [cljsjs/react-bootstrap "1.3.0-0"]
                                   [enlive "1.1.6"]                                   ]
                    }
             :test {:dependencies [
                                   [clj-http "3.10.1"]
                                   [org.clojure/data.xml "0.0.8"]
                                   [compojure "1.6.1"]
                                   [cljs-ajax "0.8.0"]
                                   [cheshire "5.10.0"]
                                   [reagent "1.0.0-alpha2"]
                                   [com.bhauman/rebel-readline-cljs "0.1.4"]
                                   [clj-time "0.15.2"]
                                   [metosin/reitit-frontend "0.5.5"]
                                   [clj-oauth "1.5.5"]
                                   [enlive "1.1.6"]                                   ]
                    }}

  )