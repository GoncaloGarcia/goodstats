(ns goodstats.oauth
  (:require [ajax.core :as ajax]))


(defn login-goodreads
  []
  (ajax/ajax-request {:uri             "http://134.122.9.217/auth"
                      :method          :get
                      :format          (ajax/json-request-format)
                      :response-format (ajax/json-response-format {:keywords? true})
                      :handler         (fn [response] (do
                                                        (js/console.log response)
                                                        (.open js/window (str (second response)))
                                                        ))}))
