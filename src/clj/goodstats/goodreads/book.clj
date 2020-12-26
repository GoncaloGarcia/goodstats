(ns goodstats.goodreads.book
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [goodstats.cache.client :as cache]
            [clojure.string :as strings]))



(defn get-book-genres
  [html]
  (let [genres (html/select
                 (html/html-snippet
                   html)
                 [:div.left :a.bookPageGenreLink])]
    (->> genres
         (map :content)
         (flatten)
         )))

(defn get-book-cover
  [html]
  (get-in (first (html/select (html/html-snippet html) [:img#coverImage])) [:attrs :src]))


(defn get-books-with-extra-data
  [url]
  (let [cached (cache/fetch url)]
    (if (not (nil? cached))
      cached
      (let [html (:body (client/get url))
            result {:book-genres (get-book-genres html)
                    :book-cover  (get-book-cover html)}]
        (do
          (cache/store url result)
          result)))))
