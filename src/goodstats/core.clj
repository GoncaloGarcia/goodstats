(ns goodstats.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.xml :as data]
            [clojure.walk :as walk]))


(defn get-parsed-book
  "Retrieves Goodreads book data by id and returns a parsed XML structure"
  [book-id key]
  (data/parse-str
    ((let [url (str "https://www.goodreads.com/book/show/" book-id ".xml")]
       (client/get url {:query-params {"key" key}}))
     :body)))

(defn get-parsed-book-content
  "Retrieves just the book content from the GoodreadsResponse"
  [parsed-book]
  (as-> parsed-book b
        (get b :content)
        (second b)
        (get b :content)))

(defn parsed-book-content->map
  "Converts the content's XML structure to a normal map"
  [parsed-book-content]
  (reduce #(conj %1 %2)
          (map (fn xml->map [elem]
                 (if (= (type elem) clojure.data.xml.Element)
                   (let [key (get elem :tag)
                         val (get elem :content)
                         attrs (get elem :attrs)]
                     (if (= :shelf key)
                       (hash-map key (map xml->map attrs))
                       (hash-map key (map xml->map val))))
                   elem)
                 )
               parsed-book-content)))

(defn filter-unecessary-keys
  "Filters map keys that aren't necessary and clutter the data"
  [book]
  (apply dissoc book [:buy_links :book_links :reviews_widget]))

(defn get-book
  "Fetches book data from the Goodreads API"
  [book-id]
  (let [content (get-parsed-book-content (get-parsed-book book-id (System/getenv "API_KEY")))]
    (filter-unecessary-keys (parsed-book-content->map content))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


