(ns goodstats.consumer.core
  (:gen-class)
  (:require [taoensso.timbre :as timbre]
            [goodstats.consumer.stats :as stats]
            [goodstats.consumer.queue-subscriber :as subscriber]))


(defn -main
  []
  (subscriber/init stats/handle-message))