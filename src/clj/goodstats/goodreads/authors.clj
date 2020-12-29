(ns goodstats.goodreads.authors
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [goodstats.cache.client :as cache]
            [clojure.string :as strings]
            [taoensso.timbre :as timbre]
            [org.httpkit.client :as http]))



(defn get-author-country
  [urls]
  (let [from-cache (map #(hash-map :url % :value (cache/fetch %)) urls)
        cached (map :value (filter #(not (nil? (:value %))) from-cache))
        not-cached (map :url (filter #(nil? (:value %)) from-cache))
        futures (doall (map http/get not-cached))
        fetched (for [resp futures]
                  (let [response @resp
                        body (:body response)
                        url (get-in response [:opts :url])
                        strings-in-div (html/select (html/html-snippet body) [:div.rightContainer html/text-node])
                        index-of-hometown (+ 1 (.indexOf strings-in-div "Born"))
                        country (-> (nth strings-in-div index-of-hometown)
                                    (strings/split #",")
                                    (last)
                                    (strings/replace "\n" "")
                                    (strings/trim)
                                    (strings/replace #"^in" "")
                                    (strings/trim))]
                    (cache/store url country)
                    country))]
    (concat fetched cached)))
