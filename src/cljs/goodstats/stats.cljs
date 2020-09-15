(ns goodstats.stats
  (:require [reagent.ratom :as ratom]
            [ajax.core :as ajax]))


(defn statistics-component [match]
  (let [books (ratom/atom "")
        id (get-in match [:parameters :query :oauth_token])]
    (ajax/ajax-request {:uri             (str "/user/" 38937510 "/stats")
                        :method          :get
                        :format          (ajax/json-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :handler         (fn [response] (reset! books (second response)))})
    (fn [] [:div {:class "fl-100 pa3" }
            [:h1 "In 2020 you've read all of these"]
            [:div {:class "flex-wrap pa3"}
             (map (fn [book]
                    ^{:key book}
                    [:img {:src (:image_url book) :class "pa3"}])
                  (:all @books))]])))







