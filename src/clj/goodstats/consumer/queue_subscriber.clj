(ns goodstats.consumer.queue-subscriber
  (:require [langohr.core :as rabbit]
            [langohr.channel :as channel]
            [langohr.queue :as queue]
            [langohr.consumers :as consumers]
            [taoensso.timbre :as timbre]
            [com.climate.claypoole :as cp])
  (:import (java.util.concurrent Executors)))


(def started (atom false))


(defn init
  [f]
  (let [num_threads (Integer/parseInt (System/getenv "NUM_THREADS"))
        conn (rabbit/connect {:uri      (System/getenv "RABBITMQ_URL")
                              :executor (Executors/newFixedThreadPool num_threads)})]

    (doseq [i (range 0 num_threads)]
      (let [chnl (channel/open conn)
            queue-name "goodstats.queue"]
        (timbre/info "Starting AMQP subscriber: " i " at: " (System/getenv "RABBITMQ_URL"))
        (queue/declare chnl queue-name {:exclusive false :auto-delete false})
        (consumers/subscribe chnl queue-name (consumers/ack-unless-exception f) {:auto-ack false})))))

