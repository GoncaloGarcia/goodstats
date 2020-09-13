(ns goodstats.shelf
  (:require [clojure.data.xml :as data]
            [clj-http.client :as client]
            [goodstats.parser :as parser]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [oauth.client :as oauth]))

(defn get-shelf-books-response
  "Retrieves Goodreads Shelf data by id and returns
  a parsed XML structure"
  [user key oauth-client access-token]
  (data/parse-str
    ((let [url (str "https://www.goodreads.com/review/list/" user ".xml")
           params {:v        2
                   :key      key
                   :shelf    ""
                   :per_page 200}
           credentials (oauth/credentials oauth-client
                                          (:oauth_token access-token)
                                          (:oauth_token_secret access-token)
                                          :GET
                                          url
                                          params)]
       (client/get url {:query-params (merge params credentials)
                        :debug        true}))
     :body)))

(defn get-user-books
  "Retrieves a user's books from Goodreads"
  [user oauth-client access-token]
  (->> (get-shelf-books-response user (System/getenv "API_KEY") oauth-client access-token)
       (parser/get-request-content)
       (parser/xml->map)
       (map first)
       (map val)
       (map parser/map-list->map)))

(defn get-books-by-shelf
  "Returns the books grouped by shelf"
  [books]
  (group-by
    (fn [book]
      (get-in book [:shelves :name]))
    books))

(defn get-books-in-shelf
  "Returns the books in a given shelf"
  [shelf books]
  (get (get-books-by-shelf books) shelf))

(defn get-this-years-books
  "Returns the books added this year"
  [books]
  (let [formatter (f/formatter "E MMMM dd H:m:s Z YYYY")
        current-year-epoch (c/to-long (t/date-time (t/year (l/local-now))))]
    (filter (fn [date]
              (let [date-added (c/to-long (f/parse formatter (date :date_added)))]
                (> date-added current-year-epoch)))
            books)))





