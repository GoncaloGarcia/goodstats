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
  (reduce
   (fn [elem1 elem2]
     (let [entry (first elem2)
           key (key entry)
           value (val entry)]
       (conj elem1
             (if (= (type value) clojure.lang.LazySeq)
               (hash-map key (map-list->map value))
               elem2))))
   {}
   list))

(defn xml->map
  "Recursively transforms an XML Element into a map of each tag associated to
  its content"
  [xml]
  (map (fn convert [elem]
         (if (= (type elem) clojure.data.xml.Element)
           (let [key (get elem :tag)
                 val (get elem :content)
                 attrs (get elem :attrs)]
             (cond
               (= :shelf key) attrs
               :else (hash-map key
                               (if (and
                                    (not (= clojure.data.xml.Element (type (first (get elem :content)))))
                                    (= (count (get elem :content)) 1))
                                 (first (get elem :content))
                                 (map convert val)))))
           elem))
       xml))
