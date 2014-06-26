(ns bubble-ring.main
  (:require [bubble.core :as bubble]
            [org.httpkit.server :as http]
            [clojure.tools.nrepl.server :as nrepl]))

(def ^:dynamic *bubble* nil)

(defn -main
  []
  (let [bubble (bubble/init)]
    (bubble/blow bubble
                 '[[(ns bubble-ring.core)
                    (defn handler [req] nil)]])
    (http/run-server (fn [req]
                       ((bubble/through bubble 'bubble-ring.core/handler) req))
                     {:port 8090})
    (binding [*bubble* bubble]
      (nrepl/start-server :port 9090))
    (println "Started nrepl on port 9090 and http on port 8090")))
