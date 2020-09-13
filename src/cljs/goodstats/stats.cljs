(ns goodstats.stats
  (:require [reagent.ratom :as ratom]
            [ajax.core :as ajax]))


(defn statistics-component [match]
  (let [books (ratom/atom "")
        id (get-in match [:parameters :query :oauth_token])]
    (ajax/ajax-request {:uri             (str "/user/" id "/stats")
                        :method          :get
                        :format          (ajax/json-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :handler         (fn [response] (reset! books (second response)))})
    (fn [] [:div
            [:ul [:h1 "Top 5 longest"]
             (map (fn [book]
                    ^{:key book}
                    [:li " Book: " (:title book)
                     [:ul
                      [:li " Pages: " (:num_pages book)]]])
                  (:top-5-longest @books))]

            [:ul [:h1 "Top 5 shortest"]
             (map (fn [book]
                    ^{:key book}
                    [:li " Book: " (:title book)
                     [:ul
                      [:li " Pages: " (:num_pages book)]]])
                  (:bottom-5-longest @books))]
            [:ul [:h1 "Top 5 Fastest"]
             (map (fn [book]
                    ^{:key book}
                    [:li " Book: " (:title book)
                     [:ul
                      [:li " Took: " (:read-time-days book) " days"]]])
                  (:top-5-fastest @books))]
            [:ul [:h1 "Top 5 Slowest"]
             (map (fn [book]
                    ^{:key book}
                    [:li " Book: " (:title book)
                     [:ul
                      [:li " Took: " (:read-time-days book) " days"]]])
                  (:bottom-5-fastest @books))]])))







