(ns goodstats.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [goodstats.oauth :as oauth]
            [compojure.route :as route]
            [goodstats.stats :as stats]
            [org.httpkit.server :as server]
            [cheshire.core :as json]))

(defonce ^:private api-server (atom nil))

(defroutes handler
           (GET "/user/:id/stats" [id]
             (json/generate-string
               (let [access-token (oauth/get-access-token id)
                     auth-user-id (oauth/get-auth-user-id access-token)]
                 (stats/do-stats auth-user-id oauth/oauth-client access-token))))
           (GET "/auth" []
             (json/generate-string (oauth/get-approve-url)))
           (route/not-found "Page not found"))

(defn create-server
  "A ring-based server listening to all http requests
  port is an Integer greater than 128"
  [port]
  (reset! api-server (server/run-server () #'handler {:port port})))

(defn stop-server
  "Gracefully shutdown the server, waiting 100ms "
  []
  (when-not (nil? @api-server)
    (@api-server :timeout 100)
    (reset! api-server nil)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment
  "Example commands for REPL use"
  (create-server 8080)
  (stop-server))

