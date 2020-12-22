(ns goodstats.book
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
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
  [urls]
  (let [promises (doall (map http/get urls))
        results (doall (map deref promises))]
    (->> results
         (map :body)
         (map (fn [book] {:book-genres (get-book-genres book)
                          :book-cover  (get-book-cover book)})))))
