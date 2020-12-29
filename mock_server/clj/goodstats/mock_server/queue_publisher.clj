(ns goodstats.mock_server.queue-publisher
  (:require [langohr.core :as rabbit]
            [langohr.channel :as channel]
            [langohr.queue :as queue]
            [langohr.basic :as rabbit-basic]
            [taoensso.timbre :as timbre]))


(def config (atom {}))


(defn init
  []
  (if (empty? @config)
    (let [conn (rabbit/connect {:uri (System/getenv "RABBITMQ_URL")})
          chnl (channel/open conn)
          queue-name "goodstats.queue"]
      (timbre/info "Starting AMQP publisher at: " (System/getenv "RABBITMQ_URL"))
      (queue/declare chnl queue-name {:exclusive false :auto-delete false})
      (reset! config {:connection conn
                      :channel       chnl
                      :queue      queue-name}))))

(defn publish
  [msg]
  (if (empty? @config)
    (throw (IllegalStateException. "Publisher is not init-ed"))
    (rabbit-basic/publish (:channel @config)
                          ""
                          (:queue @config)
                          msg
                          {:content-type "text/plain" :type "token"})))



