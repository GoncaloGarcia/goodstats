(ns package
  (:require [badigeon.bundle :refer [bundle make-out-path]]
            [badigeon.compile :as c]))

(defn -main []
      (bundle (make-out-path 'lib nil))
      (c/compile '[goodstats.web.core goodstats.consumer.core goodstats.mock_server.core] {:compile-path "target/classes"}))