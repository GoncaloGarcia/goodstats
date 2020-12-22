(ns goodstats.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [goodstats.oauth :as oauth]
            [compojure.route :as route]
            [goodstats.stats :as stats]
            [taoensso.timbre :as timbre :refer [info]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [org.httpkit.server :as server]
            [cheshire.core :as json]))

(defonce ^:private api-server (atom nil))


(defn stats
  [id]
  (let [access-token (oauth/get-access-token id)
        auth-user-id (oauth/get-auth-user-id access-token)]
    (json/generate-string (stats/do-stats auth-user-id oauth/oauth-client access-token))))

(defn auth
  []
  (json/generate-string (oauth/get-approve-url)))

(defroutes handler
           (GET "/user/:id/stats" [id] (stats id))
           (GET "/auth" [] (auth))
           (route/not-found "Page not found"))

(def app
  (-> handler
      (wrap-cors
        :access-control-allow-origin [#".*"]
        :access-control-allow-headers #{"accept"
                                        "accept-encoding"
                                        "accept-language"
                                        "authorization"
                                        "content-type"
                                        "origin"}
        :access-control-allow-methods [:get])))

(defn create-server
  "A ring-based server listening to all http requests
  port is an Integer greater than 128"
  [port]
  (reset! api-server (server/run-server #'app {:port port})))

(defn stop-server
  "Gracefully shutdown the server, waiting 100ms "
  []
  (when-not (nil? @api-server)
    (@api-server :timeout 100)
    (reset! api-server nil)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [port (System/getenv "PORT")]
    (do
      (timbre/info (str "Starting HTTP server on port: " port))
      (create-server (Integer/parseInt port)))))

(comment
  "Example commands for REPL use"
  (create-server 8080)
  (stop-server))

