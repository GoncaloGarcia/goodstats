(ns goodstats.authors
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [clojure.string :as strings]))

(defn get-author-country
  [url]
  (let [html (:body (client/get url))
        strings-in-div (html/select
                         (html/html-snippet
                           html)
                         [:div.rightContainer html/text-node])
        index-of-hometown (+ 1 (.indexOf strings-in-div "Born"))]
    (-> (nth strings-in-div index-of-hometown)
        (strings/split #",")
        (last)
        (strings/replace "\n" "")
        (strings/trim)
        (strings/replace #"^in" "")
        )))


