(ns goodstats.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.xml :as data]
            [clojure.walk :as walk]
            [goodstats.parser :as parser]))


(defn get-book-response
  "Retrieves Goodreads book data by id and returns a parsed XML structure"
  [book-id key]
  (data/parse-str
    ((let [url (str "https://www.goodreads.com/book/show/" book-id ".xml")]
       (client/get url {:query-params {"key" key}}))
     :body)))

(defn get-book
  "Fetches book data from the Goodreads API"
  [book-id]
  (as-> (parser/get-request-content (get-book-response book-id (System/getenv "API_KEY"))) book-content
        (parser/xml->map book-content)
        (parser/map-list->map book-content)
        (parser/filter-unecessary-keys book-content :buy_links :book_links :reviews_widget)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


