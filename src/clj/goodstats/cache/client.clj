(ns goodstats.cache.client
  (:require [taoensso.carmine :as carmine :refer [wcar]]))

(defonce redis-connection {:pool {} :spec {:uri (System/getenv "REDIS_URL")}})

(defn store
  [key data]
  (wcar redis-connection (carmine/set key data)))

(defn fetch
  [key]
  (wcar redis-connection (carmine/get key)))

(defn evict
  [key]
  (wcar redis-connection (carmine/del key)))