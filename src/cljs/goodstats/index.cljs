(ns goodstats.index
  (:require
    [reitit.frontend :as rf]
    [reitit.frontend.easy :as rfe]
    [reagent.dom :as rdom]
    [reagent.ratom :as ratom]
    [hodgepodge.core :refer [local-storage]]
    [goodstats.stats :as stats]
    [goodstats.oauth :as oauth]
    [ajax.core :as ajax]))

(defonce match (ratom/atom nil))

(defn current-page []
  []
  (if (not-empty (get local-storage :reading-review-data {}))
    [stats/statistics-component @match]
    (if @match
      (let [view (:view (:data @match))]
        [view @match])
      [:div {:class "vh-100 w-100 h-100 bg-gold pa2 center flex items-center"}
       [:div {:class "flex-column"}
        [:h1 {:class "f-headline-ns f1 lh-title tracked-tight tc lh-solid b v-mid near-black"}
         "Welcome to your 2020 Reading Review"]
        [:h1 {:styles #js {:cursor "pointer"} :class "underline link dim f3s lh-title tracked-tight lh-solid b v-mid near-black tc" :onClick oauth/login-goodreads}
         "Log-in with Goodreads"]]])))

(defn loading-component [match]
  (let [id (get-in match [:parameters :query :oauth_token])]
    (ajax/ajax-request {:uri             (str "http://localhost:8080/user/" id "/stats")
                        :method          :get
                        :format          (ajax/json-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :handler         (fn [response] (do
                                                          (assoc! local-storage :reading-review-data (second response))
                                                          (rfe/push-state ::results)))})
    [:div {:class "vh-100 w-100 h-100 bg-gold pa2 flex items-center"}
     [:h1 {:class "f-headline-ns f1 lh-title tracked-tight tc lh-solid b v-mid near-black"}
      (str "Please wait while our team of librarians processes your data")]]))


(def routes
  [["/loading"
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
