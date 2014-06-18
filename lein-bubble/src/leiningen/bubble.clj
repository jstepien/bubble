(ns leiningen.bubble
  (:require [leiningen.core.main :as main]
            [clojure.string :as str]
            [clojure.tools.nrepl :as nrepl]))

(defn- usage
  []
  (main/abort "Usage:\n  lein blow host port bubble-var files..."))

(defn- generate-code
  [bubble files]
  `(bubble.core/blow
     ~(symbol bubble)
     '~(mapv #(read-string (str \[ (slurp %) \]))
             files)))

(defn- blow
  [host port bubble & files]
  (with-open [conn (nrepl/connect :port (Long/parseLong port))]
    (let [client (nrepl/client conn 1000)
          code (pr-str (generate-code bubble files))]
      (doseq [{:keys [err]} (nrepl/message client {:op :eval, :code code})]
        (if err
          (main/abort (str/trim err)))))))

(defn bubble
  "Blows bubbles"
  ([project]
   (usage))
  ([project cmd & args]
   (case cmd
     "blow" (apply blow args)
     (usage))))
