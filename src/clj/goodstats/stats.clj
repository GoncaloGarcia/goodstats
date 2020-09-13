(ns goodstats.stats
  (:require [goodstats.shelf :as books]
            [clj-time.format :as f]
            [clj-time.core :as t]))


(defn books-sorted-by-page-count
  "Sorts the books by their page count"
  [books]
  (let [books-with-page-count (filter (fn [books] (= (type (get-in books [:book :num_pages])) java.lang.String)) books)]
    (sort-by (fn [book] (Integer/parseInt (get-in book [:book :num_pages]))) books-with-page-count)))

(defn get-top-5-biggest
  "Returns the 5 largest books from the list provided as argument"
  [books]
  (reverse (map #(select-keys (%1 :book) [:title :num_pages]) (take-last 5 books))))

(defn get-top-5-smallest
  "Returns the 5 smallest books from the list provided as argument"
  [books]
  (map #(select-keys (%1 :book) [:title :num_pages]) (take 5 books)))

(defn books-sorted-by-read-time
  "Sorts the books by the time it took to read them"
  [books]
  (let [formatter (f/formatter "E MMMM dd H:m:s Z YYYY")]
    (sort-by :read-time-days
             (map
               (fn [book]
                 (let [start-date-parsed (f/parse formatter (book :started_at))
                       finish-date-parsed (f/parse formatter (book :read_at))
                       interval-in-days (float (/ (t/in-hours (t/interval start-date-parsed finish-date-parsed)) 24))]
                   (assoc book :read-time-days interval-in-days)))
               books))))

(defn get-top-5-fastest
  "Returns the 5 books read the fastest from the list provided as argument"
  [books]
  (map
    #(assoc (select-keys (%1 :book) [:title]) :read-time-days (%1 :read-time-days))
    (take 5 books)))

(defn get-top-5-slowest
  "Returns the 5 books read the slowest from the list provided as argument"
  [books]
  (reverse (map
             #(assoc (select-keys (%1 :book) [:title]) :read-time-days (%1 :read-time-days))
             (take-last 5 books))))

(defn do-stats
  "Returns the entire set of stats"
  [user consumer access-token]
  (let [this-years-books (books/get-this-years-books (books/get-user-books user consumer access-token) )
        books-read-this-year (books/get-books-in-shelf "read" this-years-books)
        sorted-by-page-count (books-sorted-by-page-count books-read-this-year)
        sorted-by-read-time (books-sorted-by-read-time books-read-this-year)]
    (do (println this-years-books)
        {:top-5-longest    (get-top-5-biggest sorted-by-page-count)
         :bottom-5-longest (get-top-5-smallest sorted-by-page-count)
         :top-5-fastest    (get-top-5-fastest sorted-by-read-time)
         :bottom-5-fastest (get-top-5-slowest sorted-by-read-time)})))