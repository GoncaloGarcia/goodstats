(ns goodstats.oauth
  (:require [ajax.core :as ajax]
            [goodstats.constants :refer [SERVER_ADDR]]))


(defn login-goodreads
  []
  (ajax/ajax-request {:uri             (str SERVER_ADDR "/auth")
                      :method          :get
                      :format          (ajax/json-request-format)
                      :response-format (ajax/json-response-format {:keywords? true})
                      :handler         (fn [response] (do
                                                        (js/console.log response)
                                                        (. (. js/window -location) replace (str (second response)))
                                                        ))}))
