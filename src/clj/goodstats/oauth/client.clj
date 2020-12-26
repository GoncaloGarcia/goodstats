(ns goodstats.oauth.client
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.xml :as data]
            [goodstats.goodreads.parser :as parser]
            [oauth.client :as oauth]
            [goodstats.cache.client :as cache]
            [taoensso.timbre :as timbre]))


(def oauth-client (oauth/make-consumer (System/getenv "API_KEY")
                                       (System/getenv "API_SECRET")
                                       "https://www.goodreads.com/oauth/request_token"
                                       "https://www.goodreads.com/oauth/access_token"
                                       "https://www.goodreads.com/oauth/authorize"
                                       :hmac-sha1))


(defn get-approve-url []
  "Retrieves the URL to which the user must be redirected"
  (let [token (oauth/request-token oauth-client nil)]
    (do (cache/store (str "request-" (:oauth_token token)) token)
        (oauth/user-approval-uri oauth-client (:oauth_token token)))))

(defn get-access-token [oauth_token]
  "Retrieves the token used to make authenticated calls"
  (let [access-token (cache/fetch (str "access-" oauth_token))]
    (if (nil? access-token)
      (let [request-token (cache/fetch (str "request-" oauth_token))
            access-token (oauth/access-token oauth-client request-token nil)]
        (cache/store (str "access-" oauth_token) access-token)
        access-token)
      access-token)))

(defn get-user [access_token]
  "Retrieves the authenticated user payload as XML"
  (data/parse-str ((let [url "https://www.goodreads.com/api/auth_user"
                         credentials (oauth/credentials oauth-client
                                                        (:oauth_token access_token)
                                                        (:oauth_token_secret access_token)
                                                        :GET
                                                        url)]
                     (client/get url {:query-params credentials}))
                   :body)))

(defn get-auth-user-id [request-token access_token]
  "Retrieves the authenticated user"
  (let [id (cache/fetch (str "id-" request-token))]
    (if (nil? id)
      (let [fetched_id (->> access_token
                            (get-user)
                            (parser/get-request-content)
                            (parser/xml->map)
                            (parser/map-list->map)
                            (:link)
                            (re-find #"[0-9]+"))]
        (cache/store (str "id-" (:oauth_token (cache/fetch (str "request-" request-token)))) fetched_id)
        fetched_id)
      id)))
