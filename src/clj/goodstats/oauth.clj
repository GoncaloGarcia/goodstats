(ns goodstats.oauth
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.xml :as data]
            [goodstats.parser :as parser]
            [oauth.client :as oauth]))


(def oauth-client (oauth/make-consumer (System/getenv "API_KEY")
                                       (System/getenv "API_SECRET")
                                       "https://www.goodreads.com/oauth/request_token"
                                       "https://www.goodreads.com/oauth/access_token"
                                       "https://www.goodreads.com/oauth/authorize"
                                       :hmac-sha1))

(def request-token (atom {}))

(defn get-approve-url []
  "Retrieves the URL to which the user must be redirected"
  (let [token (oauth/request-token oauth-client nil)]
    (do (swap! request-token assoc (:oauth_token token) token)
        (oauth/user-approval-uri oauth-client (:oauth_token token)))))

(defn get-access-token [oauth_token]
  "Retrieves the token used to make authenticated calls"
  (oauth/access-token oauth-client (get @request-token oauth_token) nil))

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

(defn get-auth-user-id [access_token]
  "Retrieves the authenticated user"
  (->> access_token
       (get-user)
       (parser/get-request-content)
       (parser/xml->map)
       (parser/map-list->map)
       (:link)
       (re-find #"[0-9]+")))
