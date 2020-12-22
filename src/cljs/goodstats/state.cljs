(ns goodstats.state
  (:require [reagent.ratom :as ratom]))

(def books (ratom/atom {}))