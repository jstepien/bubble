(ns bubble-ring.core
  (:require [bubble-ring.log :as log]))

(defn start!
  []
  (log/start!))

(defn stop!
  []
  (log/stop!))

(defn handler
  [req]
  (log/log! req)
  {:body "Welcome to the bubble!\n"
   :headers {"content-type" "text/plain"}})
