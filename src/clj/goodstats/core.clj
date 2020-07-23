(ns goodstats.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes handler
           ; to serve document root address
           (GET "/" [] "<p>Hello sssfrom compojure</p>")

           ; to serve static pages saved in resources/public directory
             ; if page is not found
           (route/not-found "Pasge noffffft found"))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"  ))


