(ns goodstats.goodreads.authors
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [goodstats.cache.client :as cache]
            [clojure.string :as strings]
            [taoensso.timbre :as timbre]))

(defn get-author-country
  [url]
  (let [cached (cache/fetch url)]
    (if (not (nil? cached))
      cached
      (do
        (timbre/info "Fetching author: " url)
        (let [html (:body (client/get url))
              strings-in-div (html/select (html/html-snippet html) [:div.rightContainer html/text-node])
              index-of-hometown (+ 1 (.indexOf strings-in-div "Born"))
              country (-> (nth strings-in-div index-of-hometown)
                          (strings/split #",")
                          (last)
                          (strings/replace "\n" "")
                          (strings/trim)
                          (strings/replace #"^in" "")
                          (strings/trim))]
          (do
            (cache/store url country)
            country))))))


