(ns goodstats.consumer.queue-subscriber
  (:require [langohr.core :as rabbit]
            [langohr.channel :as channel]
            [langohr.queue :as queue]
            [langohr.consumers :as consumers]
            [taoensso.timbre :as timbre]))


(def started (atom false))


(defn init
  [f]
  (if (false? @started)
    (let [conn (rabbit/connect {:uri (System/getenv "RABBITMQ_URL")})
          chnl (channel/open conn)
          queue-name "goodstats.queue"]
      (timbre/info "Starting AMQP subscriber at: " (System/getenv "RABBITMQ_URL"))
      (queue/declare chnl queue-name {:exclusive false :auto-delete false})
      (consumers/subscribe chnl queue-name f {:auto-ack false})
      (reset! started true))))

