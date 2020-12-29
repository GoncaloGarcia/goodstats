(ns goodstats.consumer.queue-subscriber
  (:require [langohr.core :as rabbit]
            [langohr.channel :as channel]
            [langohr.queue :as queue]
            [langohr.consumers :as consumers]
            [taoensso.timbre :as timbre]
            [goodstats.pool.pool :as pool]
            [com.climate.claypoole :as cp])
  (:import (java.util.concurrent Executors)))


(def started (atom false))


(defn init
  [f]
  (let [conn (rabbit/connect {:uri      (System/getenv "RABBITMQ_URL")
                              :executor (Executors/newFixedThreadPool (* 2 (cp/ncpus)))})]
    (for [i (range 0 (* 2 (cp/ncpus)))]
      (let [chnl (channel/open conn)
            queue-name "goodstats.queue"]
        (timbre/info "Starting AMQP subscriber: " i " at: " (System/getenv "RABBITMQ_URL"))
        (queue/declare chnl queue-name {:exclusive false :auto-delete false})
        (consumers/subscribe chnl queue-name (consumers/ack-unless-exception f) {:auto-ack false})))))

