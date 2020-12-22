(ns goodstats.index
  (:require
    [reitit.frontend :as rf]
    [reitit.frontend.easy :as rfe]
    [reagent.dom :as rdom]
    [reagent.ratom :as ratom]
    [hodgepodge.core :refer [local-storage]]
    [goodstats.stats :as stats]
    [goodstats.state :as state]
    [goodstats.oauth :as oauth]
    [ajax.core :as ajax]))

(defonce match (ratom/atom nil))

(defn current-page []
  [:div
   (if (not-empty (get local-storage :reading-review-data {}))
     [stats/statistics-component @match]
     (if @match
       (let [view (:view (:data @match))]
         [view @match])))
   ])

(defn loading-component [match]
  (let [id (get-in match [:parameters :query :oauth_token])]
    (ajax/ajax-request {:uri             (str "http://localhost:8080/user/" id "/stats")
                        :method          :get
                        :format          (ajax/json-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :handler         (fn [response] (do
                                                          (assoc! local-storage :reading-review-data-2 "A")
                                                          (assoc! local-storage :reading-review-data (second response))
                                                          ;(reset! state/books (second response))
                                                          (rfe/push-state ::results)))})
    [:div "Loading"]))


(def routes
  [["/login"
    {:name ::stats
     :view oauth/redirect-to-goodreads-component}]
   ["/loading"
    {:name ::load
     :view loading-component}]
   ["/results"
    {:name ::results
     :view stats/statistics-component}]])

(defn init! []
  (rfe/start!
    (rf/router routes {})
    (fn [m] (reset! match m))
    ;; set to false to enable HistoryAPI
    {:use-fragment true})
  (rdom/render [current-page] (.getElementById js/document "app")))
