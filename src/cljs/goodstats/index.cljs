(ns goodstats.index
  (:require [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reagent.dom :as rdom]
            [reagent.ratom :as ratom]
            [goodstats.stats :as stats]
            [goodstats.oauth :as oauth]))

(defonce match (ratom/atom nil))

(defn current-page []
  [:div
   [:ul
    [:li [:a {:href (rfe/href ::results)} "Stats"]]]
   (if @match
     (let [view (:view (:data @match))]
       [view @match]))
   ])


(def routes
  [["/login"
    {:name ::stats
     :view oauth/redirect-to-goodreads-component}]
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
