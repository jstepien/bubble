(ns bubble-ring.log)

;; A piece of global mutable state introduced for demonstration
;; purpose only. Avoid such things. They tend to bite.
(def ^:private log (atom ()))

(defn log!
  [req]
  (swap! log conj req))

(defn start!
  []
  (add-watch log ::key (fn [_ _ _ state]
                         (prn (count state) (first state)))))

(defn stop!
  []
  (remove-watch log ::key)
  (println (format "Served %s requests" (count @log))))
