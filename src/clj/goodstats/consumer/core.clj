(ns goodstats.consumer.core
  (:gen-class)
  (:require [taoensso.timbre :as timbre]
            [goodstats.consumer.stats :as stats]
            [goodstats.consumer.queue-subscriber :as subscriber]))


(defn -main
  []
  (let [function (if (= "test" (System/getenv "PROFILE"))
                   stats/handle-message-test
                   stats/handle-message)]
    (subscriber/init function)))