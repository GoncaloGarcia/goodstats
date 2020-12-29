(ns goodstats.goodreads.book
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [org.httpkit.client :as http]
            [goodstats.cache.client :as cache]
            [clojure.string :as strings]
            [taoensso.timbre :as timbre]
            [com.climate.claypoole :as cp]))



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
  (let [from-cache (map #(hash-map :url % :value (cache/fetch %)) urls)
        cached (map :value (filter #(not (nil? (:value %))) from-cache))
        not-cached (map :url (filter #(nil? (:value %)) from-cache))
        futures (doall (map http/get not-cached))
        fetched (for [resp futures]
                          (let [response @resp
                                body (:body response)
                                url (get-in response [:opts :url])
                                result (hash-map :book-genres (get-book-genres body)
                                                 :book-cover (get-book-cover body))]
                            (cache/store url result)
                            result))]
    (concat fetched cached)))
