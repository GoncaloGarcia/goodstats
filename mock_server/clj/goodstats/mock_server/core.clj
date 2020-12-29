(ns goodstats.mock_server.core
  (:gen-class)
  (:require
    [compojure.core :refer :all]
    [ring.middleware.cors :refer [wrap-cors]]
    [compojure.route :as route]
    [goodstats.mock_server.queue-publisher :as publisher]
    [org.httpkit.server :as server]
    [taoensso.timbre :as timbre]
    [clojure.string :as string]
    [clojure.java.io :as io]))

(defonce ^:private api-server (atom nil))


(defn handle-initial-request [id]
  (timbre/info "Publishing AMQP message")
  (publisher/publish (str (System/currentTimeMillis)))
  "OK")

(defn handle-poll-request [id] "")

(defn serve-goodreads-user-data [id]
  (Thread/sleep 2000)
  (-> (slurp (io/resource "resources/user-response.xml"))
      (string/replace "<![CDATA[https://www.goodreads.com/author/show/"
                      (str "<![CDATA[http://localhost:8082/author/show/" (rand-int 100)))
      (string/replace "<link>https://www.goodreads.com/book/show/"
                      (str "<link>http://localhost:8082/book/show/" (rand-int 100)))))

(defn serve-goodreads-book-data [id]
  (Thread/sleep 2000)
  (slurp (io/resource "resources/book-response.html")))

(defn serve-goodreads-author-data [id]
  (Thread/sleep 2000)
  (slurp (io/resource "resources/author-response.html")))




(defroutes handler
           (POST "/user/:id/stats" [id] (handle-initial-request id))
           (GET "/user/:id/stats" [id] (handle-poll-request id))
           (GET "/review/list/:id" [id] (serve-goodreads-user-data id))
           (GET "/author/show/:id" [id] (serve-goodreads-author-data id))
           (GET "/book/show/:id" [id] (serve-goodreads-book-data id))
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
  (let [port "8082"]
    (timbre/info (str "Starting HTTP server on port: " port))
    (publisher/init)
    (create-server (Integer/parseInt port))))

; REPL stuff
(comment
  (stop-server)
  (create-server 8082))