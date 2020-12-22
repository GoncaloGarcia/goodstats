(ns goodstats.authors
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojure.string :as strings]))



(defn get-country
  [html]
  (let [strings-in-div (html/select
                         (html/html-snippet
                           html)
                         [:div.rightContainer html/text-node])
        index-of-hometown (+ 1 (.indexOf strings-in-div "Born"))]

    (try (-> (nth strings-in-div index-of-hometown)
             (strings/split #",")
             (last)
             (strings/replace "\n" "")
             (strings/trim)
             (strings/replace #"^in" "")
             (strings/trim)
             )
         (catch Exception e (str "caught exception: " (.getMessage e))))))

(defn get-author-country
  [urls]
  (let [promises (doall (map http/get urls))
        results (doall (map deref promises))]
    (->> results
         (map :body)
         (map get-country)))
  )