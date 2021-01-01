(ns goodstats.goodreads.shelf
  (:require [clojure.data.xml :as data]
            [clj-http.client :as client]
            [goodstats.goodreads.parser :as parser]
            [goodstats.goodreads.authors :as authors]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [oauth.client :as oauth]
            [clojure.tools.reader.edn :as edn]
            [com.climate.claypoole :as cp]))

(defn get-shelf-books-response
  "Retrieves Goodreads Shelf data by id and returns
  a parsed XML structure"
  [user key consumer token page]
  (data/parse-str
    ((let [url (str (System/getenv "GOODREADS_URL") "/review/list/" user ".xml")
           params {:v        2
                   :key      key
                   :shelf    ""
                   :page     page
                   :per_page 200}
           credentials (oauth/credentials consumer
                                          (:oauth_token token)
                                          (:oauth_token_secret token)
                                          :GET
                                          url
                                          params)]
       (client/get url {:query-params (merge params credentials)}))
     :body)))

(defn get-user-books
  "Retrieves a user's books from Goodreads"
  [user consumer token]
  (loop [i 1
         books '()]
    (let [response (->> (get-shelf-books-response user (System/getenv "API_KEY") consumer token i)
                        (parser/get-request-content)
                        (parser/xml->map)
                        (map first)
                        (map val)
                        (map parser/map-list->map))
          updated-books (concat books response)]
      (if (> 200 (count response))
        updated-books
        (recur (inc i) updated-books)))))

(defn get-books-by-shelf
  "Returns the books grouped by shelf"
  [books]
  (group-by
    (fn [book]
      (get-in book [:shelves :name]))
    books))

(defn get-books-in-shelf
  "Returns the books in a given shelf"
  [shelf books]
  (get (get-books-by-shelf books) shelf))

(defn flatten-author-and-book
  "Returns a flat map with the author keys changed to author-*key*"
  [books-with-authors]
  (map #(let [book (dissoc (get-in %1 [:book]) :authors)
              rating {:rating (get %1 :rating)}
              author (get-in %1 [:book :authors :author])
              author-keys (map (fn [key] (keyword (str "author-" (name key)))) (keys author))
              author-key-map (zipmap (keys author) author-keys)]
          (merge book rating (clojure.set/rename-keys author author-key-map)))
       books-with-authors))

(defn combine-author-books
  "Reduces a list of maps into a single map with different values joined into a list"
  [author]
  (reduce
    (fn [book1 book2]
      (merge-with
        #(if (= %1 %2)
           %1
           (flatten (list %1 %2)))
        book1 book2))
    author))

(defn get-books-by-author
  "Returns the books grouped by author id"
  [books]
  (let [books-with-authors (filter #(> (count (get-in %1 [:book :authors])) 0) books)]
    (as-> books-with-authors b
          (flatten-author-and-book b)
          (map #(select-keys %1 [:rating :author-link :author-id :author-name :author-image_url :title]) b)
          (group-by :author-id b)
          (map #(combine-author-books (val %1)) b)
          (let [all b
                all-countries (map #(hash-map :country %)
                                   (authors/get-author-country (map :author-link all)))]
            (map #(merge (key %) (val %)) (zipmap b all-countries)))
          (map #(merge %1 (let [elements (map edn/read-string (flatten (list (:rating %1))))
                                avg (/ (reduce + elements) (count elements))]
                            {:avg-rating avg})) b)
          )))

(defn get-this-years-books
  "Returns the books added this year"
  ([books] (get-this-years-books books :date_added))
  ([books key]
   (let [formatter (f/formatter "E MMMM dd H:m:s Z YYYY")
         last-day (clj-time.coerce/to-long "2021-01-01T00:00:00.000Z")
         first-day (clj-time.coerce/to-long "2020-01-01T00:00:00.000Z")]
     (filter (fn [date]
               (let [date-added (c/to-long (f/parse formatter (date key)))]
                 (and (> date-added first-day)
                      (< date-added last-day))))
             books))))





