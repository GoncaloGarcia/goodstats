(ns goodstats.web.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [goodstats.oauth.client :as oauth]
            [compojure.route :as route]
            [goodstats.web.queue-publisher :as publisher]
            [taoensso.timbre :as timbre :refer [info]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [org.httpkit.server :as server]
            [cheshire.core :as json]
            [goodstats.cache.client :as cache]))

(defonce ^:private api-server (atom nil))


(defn publish-stats-request
  [id]
  (let [
        access-token (oauth/get-access-token id)
        auth-user-id (oauth/get-auth-user-id id access-token)
        cached-value (cache/fetch (str "result-" auth-user-id))]
    (timbre/info (str "Received request to compute stats for: " auth-user-id))
    (timbre/debug (str "Cached value was: " cached-value))
    (if (nil? cached-value)
      (do
        (timbre/info "Published AMQP message")
        (publisher/publish id))))
  (json/generate-string "OK"))

(defn fetch-stats-result
  [id]
  (let [auth-user-id (oauth/get-auth-user-id id nil)
        results (cache/fetch (str "result-" auth-user-id))]
    (timbre/info (str "Attempting to fetch stats for: " auth-user-id))
    (if (nil? results)
      (if (nil? (:error results))
        (json/generate-string "")
        (cache/evict (str "result-" auth-user-id)))
      (json/generate-string results))))

(defn auth
  []
  (json/generate-string (oauth/get-approve-url)))

(defroutes handler
           (POST "/user/:id/stats" [id] (publish-stats-request id))
           (GET "/user/:id/stats" [id] (fetch-stats-result id))
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
        :access-control-allow-methods [:get :post])))

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
    (timbre/info (str "Starting HTTP server on port: " port))
    (publisher/init)
    (create-server (Integer/parseInt port))))



(comment
  "Example commands for REPL use"
  (create-server 8080)
  (stop-server))


