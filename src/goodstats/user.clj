(ns goodstats.user
  (:gen-class)
  (:require [clojure.data.xml :as data]
            [clj-http.client :as client]
            [goodstats.parser :as parser]
            [goodstats.book :as book]))

(defn get-shelf-response
  "Retrieves Goodreads User shelves data by id and returns
  a parsed XML structure"
  [user key]
  (data/parse-str
   ((let [url "https://www.goodreads.com/shelf/list.xml"]
      (client/get url {:query-params {"key"     key
                                      "user_id" user}}))
    :body)))

(defn shelf->map
  "Converts a shelf XML Element to a Map of each tag associated to its content"
  [shelf-content]
  (parser/map-list->map (shelf-content :user_shelf)))

(defn get-user-shelves
  "Retrieves a map representing a Goodreads user's shelves"
  [user]
  (let [token (System/getenv "API_KEY")]
    (parser/map-list->map
     (map (fn [entry]
            (let [value (shelf->map entry)]
              (hash-map (keyword (first (value :name))) value)))
          (->> (get-shelf-response user token)
               (parser/get-request-content)
               (parser/xml->map))))))


