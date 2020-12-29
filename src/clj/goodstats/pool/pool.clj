(ns goodstats.pool.pool
  (:require [com.climate.claypoole :as cp]))

(def thread-pool (cp/threadpool (* 2 (cp/ncpus))))