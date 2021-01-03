(ns goodstats.consumer.stats
  (:require [goodstats.goodreads.shelf :as books]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clojure.set :as set]
            [clojure.edn :as edn]
            [goodstats.cache.client :as cache]
            [iso-country-codes.core :as codes]
            [goodstats.goodreads.book :as book]
            [clojure.string :as string]
            [goodstats.oauth.client :as oauth]
            [taoensso.timbre :as timbre]
            [com.climate.claypoole :as cp]))


(defn filter-empty-key
  [books key]
  (filter #(not-empty (key %1))
          books))


(defn books-sorted-by-page-count
  "Sorts the books by their page count"
  [books]
  (let [books-with-page-count (filter (fn [books] (= (type (get-in books [:book :num_pages])) java.lang.String)) books)]
    (sort-by (fn [book] (Integer/parseInt (get-in book [:book :num_pages]))) books-with-page-count)))

(defn get-top-5-biggest
  "Returns the 5 largest books from the list provided as argument"
  [books]
  (reverse (map #(select-keys (%1 :book) [:title_without_series :num_pages]) (take-last 5 books))))

(defn get-top-5-smallest
  "Returns the 5 smallest books from the list provided as argument"
  [books]
  (map #(select-keys (%1 :book) [:title_without_series :num_pages]) (take 5 books)))

(defn books-sorted-by-read-time
  "Sorts the books by the time it took to read them"
  [books]
  (let [formatter (f/formatter "E MMMM dd H:m:s Z YYYY")
        filtered-books (filter-empty-key (filter-empty-key books :started_at) :read_at)]
    (sort-by :read-time-days
             (map
               (fn [book]
                 (let [start-date-parsed (f/parse formatter (book :started_at))
                       finish-date-parsed (f/parse formatter (book :read_at))
                       interval-in-days (float (/ (t/in-hours (t/interval start-date-parsed finish-date-parsed)) 24))]
                   (assoc book :read-time-days interval-in-days)))
               filtered-books))))

(defn books-with-review-and-page-count
  [books]
  (let [books-with-page-count (filter (fn [books] (= (type (get-in books [:book :num_pages])) java.lang.String)) books)]
    (map #(hash-map
            :title (get-in % [:book :title_without_series])
            :rating (Integer/parseInt (:rating %))
            :pages (Integer/parseInt (get-in % [:book :num_pages])))
         books-with-page-count)))

(defn books-grouped-by-read-time
  [sorted-by-read-time]
  (->> sorted-by-read-time
       (reduce
         (fn [prev book]
           (let [read-time (:read-time-days book)
                 title (get-in book [:book :title_without_series])
                 book-data {:read-time-days read-time :title title}]
             (cond
               (< read-time 7) (update-in prev [:1week :books] conj book-data)
               (< read-time 14) (update-in prev [:2week :books] conj book-data)
               (< read-time 30) (update-in prev [:1month :books] conj book-data)
               (< read-time 60) (update-in prev [:2month :books] conj book-data)
               (< read-time 120) (update-in prev [:4month :books] conj book-data)
               (< read-time 240) (update-in prev [:8month :books] conj book-data)
               :else (update-in prev [:1year :books] conj book-data))))
         {:1week  {:name "1 week" :fill "#9EEBCF" :count 0 :books '()}
          :2week  {:name "2 weeks" :fill "#FF6300" :count 0 :books '()}
          :1month {:name "1 month" :fill "#19A974" :count 0 :books '()}
          :2month {:name "2 months" :fill "#FFD700" :count 0 :books '()}
          :4month {:name "4 months" :fill "#00449E" :count 0 :books '()}
          :8month {:name "8 months" :fill "#A463F2" :count 0 :books '()}
          :1year  {:name "1 year" :fill "#FF80CC" :count 0 :books '()}})
       (map
         (fn [elem]
           (update-in (val elem) [:count] + (count (:books (val elem))))))))

(defn get-top-5-fastest
  "Returns the 5 books read the fastest from the list provided as argument"
  [books]
  (map
    #(assoc (select-keys (%1 :book) [:title_without_series]) :read-time-days (%1 :read-time-days))
    (take 5 books)))

(defn get-top-5-slowest
  "Returns the 5 books read the slowest from the list provided as argument"
  [books]
  (reverse (map
             #(assoc (select-keys (%1 :book) [:title_without_series]) :read-time-days (%1 :read-time-days))
             (take-last 5 books))))

(defn authors-with-review-count-and-score
  [authors]
  (let [authors-with-reviews-and-score
        (map #(hash-map
                :author (:author-name %)
                :review-count (count (flatten (list (:title %))))
                :review-score (Float/parseFloat (format "%.2f" (float (:avg-rating %)))))
             authors)]
    authors-with-reviews-and-score))

(defn authors-grouped-by-country
  [authors]
  (let [authors-with-country (filter #(not (= "" (:country %1))) authors)]
    (map (fn [item]
           (hash-map :country (key item)
                     :value (if (not= (type (val item)) java.lang.String)
                              (reduce #(str %1 ", " %2) (val item))
                              (val item))))
         (reduce (fn [first second]
                   (merge-with #(flatten (list %1 %2)) first
                               {(codes/country-translate :name :alpha-2 (clojure.string/replace (:country second) #"The " ""))
                                (:author-name second)}))
                 {}
                 authors-with-country))))


(defn books-by-month
  [read-this-year]
  (as-> read-this-year books
        (map #(hash-map :read-at (:read_at %1) :title (get-in %1 [:book :title_without_series])) books)
        (group-by #(second (string/split (:read-at %1) #" ")) books)
        (list ["Jan" (get books "Jan")]
              ["Feb" (get books "Feb")]
              ["Mar" (get books "Mar")]
              ["Apr" (get books "Apr")]
              ["May" (get books "May")]
              ["Jun" (get books "Jun")]
              ["Jul" (get books "Jul")]
              ["Aug" (get books "Aug")]
              ["Sep" (get books "Sep")]
              ["Oct" (get books "Oct")]
              ["Nov" (get books "Nov")]
              ["Dec" (get books "Dec")])
        (map #(hash-map :month (first %1) :count (count (second %1)) :books (second %1)) books)
        (reduce (fn [[updated total] element]
                  (let [total (+ total (:count element))]
                    [(conj updated (assoc element :sum total))
                     total]))
                [[] 0] books)
        (first books))
  )

(defn books-with-extra-data
  [read-this-year]
  (as-> read-this-year b
        (book/get-books-with-extra-data (map #(get-in %1 [:book :link]) b))
        (zipmap read-this-year b)
        (map #(merge (key %) (val %)) b)))


(defn get-genres-by-frequency
  [books-with-genre]
  (->> books-with-genre
       (map :book-genres)
       (flatten)
       (frequencies)
       (map #(assoc {} :label (key %) :value (val %)))
       (sort-by :value)
       (take-last 30)))


(defn books-read-this-year
  [books]
  (books/get-this-years-books
    (filter-empty-key (books/get-books-in-shelf "read" books) :read_at)
    :read_at))

(defn do-author-stats
  [books]
  "Returns the entire set of stats for authors"
  (let [
        read-this-year (books-read-this-year books)
        authors-read-this-year (books/get-books-by-author read-this-year)
        with-count (authors-with-review-count-and-score authors-read-this-year)
        grouped-by-country (authors-grouped-by-country authors-read-this-year)]
    {:all      authors-read-this-year
     :favorite with-count
     :country  grouped-by-country}))

(defn do-genre-stats
  "Returns the entire set of stats for books"
  [read-this-year]
  {:all (get-genres-by-frequency read-this-year)})

(defn do-book-stats
  "Returns the entire set of stats for books"
  [read-this-year books]
  (let [title-added-this-year (map #(get-in % [:book :title_without_series]) (books/get-this-years-books books))
        title-read-this-year (map #(get-in % [:book :title_without_series]) read-this-year)
        sorted-by-page-count (books-sorted-by-page-count read-this-year)
        sorted-by-read-time (books-sorted-by-read-time read-this-year)
        discovered-this-year (set/intersection (set title-read-this-year) (set title-added-this-year))
        planning (set/difference (set title-read-this-year)
                                 (set title-added-this-year))]
    {:all                  (map #(merge (select-keys %1 [:book-cover]) (select-keys (:book %1) [:title_without_series])) read-this-year)
     :top-5-longest        (get-top-5-biggest sorted-by-page-count)
     :average-pages        (format "%.2f"
                                   (float (/ (reduce + (map #(Integer/parseInt (get-in % [:book :num_pages])) sorted-by-page-count))
                                             (count read-this-year))))
     :discovered-this-year {:name "Discovered this year" :count (count discovered-this-year) :data discovered-this-year}
     :planning             {:name "Planning on reading" :count (count planning) :data planning}
     :bottom-5-longest     (get-top-5-smallest sorted-by-page-count)
     :top-5-fastest        (get-top-5-fastest sorted-by-read-time)
     :rating-by-page-count (books-with-review-and-page-count read-this-year)
     :bottom-5-fastest     (get-top-5-slowest sorted-by-read-time)
     :by-month             (books-by-month read-this-year)
     :average-by-month     (format "%.2f" (float (/ (reduce + (map #(count (:books %)) (books-by-month read-this-year))) 12)))
     :by-read-time         (books-grouped-by-read-time sorted-by-read-time)}))

(defn do-stats
  [user consumer token]
  (time
    (let [books (books/get-user-books user consumer token)
          read-this-year (books-read-this-year books)
          with-extra-data (books-with-extra-data read-this-year)
          result (if (empty? read-this-year)
                   {:book-stats '()
                    :author-stats '()
                    :genre-stats '()}
                   {:book-stats   (do-book-stats with-extra-data books)
                    :author-stats (do-author-stats books)
                    :genre-stats  (do-genre-stats with-extra-data)})]
      result)))

(defn handle-message
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [id (String. payload "UTF-8")
        access-token (do
                       (timbre/info (str "Received AMQP message: " id))
                       (oauth/get-access-token id))
        auth-user-id (do
                       (timbre/info (str "Oauth token: " access-token))
                       (oauth/get-auth-user-id id access-token))]
    (timbre/info (str "Received message for user: " auth-user-id))
    (cache/store (str "result-" auth-user-id) (do-stats auth-user-id oauth/oauth-client access-token))))

(defn handle-message-test
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [id (String. payload "UTF-8")
        access-token (do
                       (timbre/info (str "Received AMQP message after: " (- (System/currentTimeMillis) (Long/parseLong id))))
                       "ABC")
        auth-user-id (do
                       (timbre/info (str "Oauth token: " access-token))
                       "ABC")]
    (timbre/info (str "Received message for user: " auth-user-id))
    (cache/store (str "result-" auth-user-id) (let [value (do-stats auth-user-id oauth/oauth-client access-token)]
                                                (timbre/info "Processed after: " (- (System/currentTimeMillis) (Long/parseLong id)))
                                                value))))



;; Setup Environment
(comment
  (do
    (:require '[oauth.client :as oauth])
    (def user (System/getenv "USER_ID"))
    (def token (System/getenv "API_KEY"))
    (def consumer (oauth.client/make-consumer token
                                              (System/getenv "API_SECRET")
                                              "https://www.goodreads.com/oauth/request_token"
                                              "https://www.goodreads.com/oauth/access_token"
                                              "https://www.goodreads.com/oauth/authorize"
                                              :hmac-sha1)))
  (do
    (def books (books/get-user-books user consumer token))
    (def read-this-year (books-read-this-year books)))
  )