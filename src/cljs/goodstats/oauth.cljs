(ns goodstats.oauth
  (:require [ajax.core :as ajax]))


(defn redirect-to-goodreads-component []
  (ajax/ajax-request {:uri             " /auth "
                      :method          :get
                      :format          (ajax/json-request-format)
                      :response-format (ajax/json-response-format {:keywords? true})
                      :handler         (fn [response] (.open js/window (str (second response))))})
  [:div])
