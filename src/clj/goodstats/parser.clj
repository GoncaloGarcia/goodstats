(ns goodstats.parser
  (:import (clojure.data.xml Element)))

(defn get-request-content
  "Retrieves just the request content from the GoodreadsResponse"
  [request]
  (as-> request b
    (get b :content)
    (second b)
    (get b :content)))

(defn filter-unecessary-keys
  "Filters map keys that aren't necessary and clutter the data"
  [map & keys]
  (apply dissoc map keys))

(defn map-list->map
  "Transforms a list of maps into a single map"
  [list]
  (reduce #(conj %1 %2) list))

(defn xml->map
  "Recursively transforms an XML Element into a map of each tag associated to
  its content"
  [xml]
  (map (fn convert [elem]
         (if (= (type elem) Element)
           (let [key (get elem :tag)
                 val (get elem :content)
                 attrs (get elem :attrs)]
             (cond
               (= :shelf key) attrs
               :else (hash-map key (map convert val))))
           elem))
       xml))
