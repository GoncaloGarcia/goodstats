(ns goodstats.shelf
  (:require [clojure.data.xml :as data]
            [clj-http.client :as client]
            [goodstats.parser :as parser]))

(defn get-shelf-books-response
  "Retrieves Goodreads Shelf data by id and returns
  a parsed XML structure"
  [user shelf key]
  (data/parse-str
   ((let [url (str "https://www.goodreads.com/review/list/" user ".xml")]
      (client/get url {:query-params {"v"   2
                                      "key" key}}))
    :body)))

(defn get-user-books
  [user shelf api-key]
  (->> (get-shelf-books-response user shelf api-key)
       (parser/get-request-content)
       (parser/xml->map)))

(defn parse-books
  [user shelf api-key]
  (let [book ((first (get-user-books user shelf api-key)) :review)]
    (parser/map-list->map book)))




