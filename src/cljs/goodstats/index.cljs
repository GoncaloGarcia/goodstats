(ns goodstats.index
  (:require
    [reitit.frontend :as rf]
    [reitit.frontend.easy :as rfe]
    [reagent.dom :as rdom]
    [reagent.ratom :as ratom]
    [hodgepodge.core :refer [local-storage]]
    [goodstats.stats :as stats]
    [goodstats.oauth :as oauth]
    [goodstats.constants :refer [SERVER_ADDR CLIENT_ADDR QUOTES]]
    [ajax.core :as ajax]))

(defonce match (ratom/atom nil))

(defn current-page
  []
  (if (not-empty (get local-storage :reading-review-data {}))
    [stats/statistics-component @match]
    (if @match
      (let [view (:view (:data @match))]
        [view @match])
      [:div {:class "vh-100 w-100 h-100 bg-gold pa2 center flex items-center justify-center"}
       [:div {:class "flex-column"}
        [:h1 {:class "f-headline-ns f1 lh-title tracked-tight tc lh-solid b v-mid near-black"}
         "Welcome to your 2020 Reading Year"]
        [:h1 {:styles #js {:cursor "pointer"} :class "underline link dim f3s lh-title tracked-tight lh-solid b v-mid near-black tc" :onClick oauth/login-goodreads}
         "Log-in with Goodreads"]
        [:h1 {:class "f5 lh-title tracked-tight lh-solid b v-mid near-black tc"}
         "If you're on iOS and have the Goodreads app installed, please open in an incognito tab and log-in through the browser. There is a bug with the app that prevents this site from working."]]])))

(defn error-page
  [match]
  [:div {:class "vh-100 w-100 h-100 bg-gold pa2 center flex items-center justify-center"}
   [:div {:class "flex-column"}
    [:h1 {:class "f-headline-ns f1 lh-title tracked-tight tc lh-solid b v-mid near-black"}
     "An error has ocurred"]
    [:h1 {:class "f3s lh-title tracked-tight lh-solid b v-mid near-black tc"}
     "Please make sure you've allowed this app."]
    [:h1 {:styles #js {:cursor "pointer"} :class "underline link dim f5 lh-title tracked-tight lh-solid b v-mid near-black tc" :onClick (fn [] (. (. js/window -location) replace CLIENT_ADDR))}
     "Click here to go back to the start"]]])


(defn loading-component [match]
  (let [id (get-in match [:parameters :query :oauth_token])
        authorized (get-in match [:parameters :query :authorize])
        quote (ratom/atom "")
        ui (fn [match] [:div {:class "login vh-100 w-100 h-100 bg-gold pa2"}
                        [:div {:class "w-100 h-50"}
                         [:h1 {:class "f-headline-ns f2 lh-title tracked-tight tc lh-solid b v-mid near-black"}
                          (str "Please keep this page open while our two librarians process your data")]]
                        [:div {:class "w-100 h-25"}
                         [:h1 {:class "f3-ns f4 lh-title tc lh-solid b v-mid near-black"}
                          "In the meantime, here are some famous quotes:"]
                         [:h1 {:class "f4-ns f5 courier lh-title tc lh-solid b v-mid near-black"}
                          @quote]]])]
    (if-not (= "0" authorized)
      (ajax/ajax-request {:uri             (str SERVER_ADDR "/user/" id "/stats")
                          :method          :post
                          :format          (ajax/json-request-format)
                          :response-format (ajax/json-response-format {:keywords? true})
                          :handler         (fn check [response]
                                             (if (= "OK" (second response))
                                               (ajax/ajax-request {:uri             (str SERVER_ADDR "/user/" id "/stats")
                                                                   :method          :get
                                                                   :format          (ajax/json-request-format)
                                                                   :response-format (ajax/json-response-format {:keywords? true})
                                                                   :handler         (fn [rsp]
                                                                                      (if-not (empty? (second rsp))
                                                                                        (do
                                                                                          (assoc! local-storage :reading-review-data (second rsp))
                                                                                          (rfe/push-state ::results))
                                                                                        (do
                                                                                          (println "Trying again")
                                                                                          (reset! quote (rand-nth QUOTES))
                                                                                          (js/setTimeout #(check [true "OK"]) 10000))))})
                                               ))}))
    (if-not (= "0" authorized)
      ui
      (do
        (println "Fail")
        (rfe/push-state ::error)
        error-page))))


(def routes
  [
   ["/error"
    {:name ::error
     :view error-page}]
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
