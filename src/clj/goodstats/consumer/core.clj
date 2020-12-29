(ns goodstats.consumer.core
  (:gen-class)
  (:require [taoensso.timbre :as timbre]
            [goodstats.consumer.stats :as stats]
            [goodstats.consumer.queue-subscriber :as subscriber]))


(defn -main
  []
  (timbre/info "Starting Goodstats consumer")
  (subscriber/init stats/handle-message))