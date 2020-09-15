(ns goodstats.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [goodstats.oauth :as oauth]
            [compojure.route :as route]
            [goodstats.stats :as stats]
            [cheshire.core :as json]))

(defroutes handler
           (GET "/user/:id/stats" [id]
             (json/generate-string
               (let [access-token (oauth/get-access-token id)
                     auth-user-id (oauth/get-auth-user-id access-token)]
                 (stats/do-stats auth-user-id oauth/oauth-client access-token))))
           (GET "/auth" []
             (json/generate-string (oauth/get-approve-url)))
           (route/not-found "Page not found"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

