(ns bubble.core
  (:refer-clojure :exclude [pop])
  (:require [bubble.blow :as blow]))

(defn init
  []
  (atom {}))

(defn through
  "There's something not right with the world when you look at it
  through a bubble."
  [bubble sym]
  {:pre [(symbol? sym)]}
  (get (:vars @bubble) sym))

(defn pop
  "Pop! and it's gone. As if it has never really existed."
  [bubble]
  (doseq [ns (:nss @bubble)]
    (remove-ns (symbol ns)))
  (swap! bubble dissoc :nss :vars))

(def blow #(apply blow/blow %&))

(alter-meta! #'blow (constantly (meta #'blow/blow)))
