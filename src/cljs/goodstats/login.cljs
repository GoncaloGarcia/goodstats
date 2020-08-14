(ns goodstats.login
  (:require [reagent.dom :as rdom]
            [reagent.ratom :as ratom]
            [goog.dom :as gdom]
            [ajax.core :as ajax]))

(defn simple-component []
  (let [book (ratom/atom "")]
    (ajax/ajax-request {:uri             "/book/1"
                        :method          :get
                        :format          (ajax/json-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :handler         (fn [response] (reset! book (second response)))})
    (fn [] [:div
            (first (:title @book))])))

(defn timer-component []
  (let [seconds-elapsed (ratom/atom 0)]                     ;; setup, and local state
    (fn []                                                  ;; inner, render function is returned
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div "Seconds Elapsed: " @seconds-elapsed])))

(rdom/render
 [simple-component]
 (gdom/getElement "root"))


