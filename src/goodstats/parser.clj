(ns goodstats.parser)

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
  "Transforms an XML Element into a map of each tag associated to
  its content"
  [xml]
  (map (fn convert [elem]
         (if (= (type elem) clojure.data.xml.Element)
           (let [key (get elem :tag)
                 val (get elem :content)
                 attrs (get elem :attrs)]
             (if (= :shelf key)
               attrs
               (hash-map key (map convert val))))
           elem))
       xml))
