(ns leiningen.bubble
  (:require [leiningen.core.main :as main]
            [clojure.string :as str]
            [clojure.tools.nrepl :as nrepl]))

(defn- usage
  []
  (main/abort "Usage:\n  lein blow host port bubble-var files..."))

(defn- generate-code
  [bubble {:keys [files after before]}]
  (let [callbacks (concat
                    (if before
                      `[:before (fn [~'through] ((~'through '~before)))])
                    (if after
                      `[:after (fn [~'through] ((~'through '~after)))]))]
    `(bubble.core/blow
       ~(symbol bubble)
       '~(mapv #(read-string (str \[ (slurp %) \]))
               files)
       ~@callbacks)))

(defn- blow
  [host port bubble & args]
  (let [parse (fn rec [[key val & others :as args]]
                (case key
                  (":after" ":before") (merge {(keyword (subs key 1))
                                               (symbol val)}
                                              (rec others))
                  {:files args}))
        opts (parse args)]
    (with-open [conn (nrepl/connect :port (Long/parseLong port))]
      (let [client (nrepl/client conn 1000)
            code (pr-str (generate-code bubble opts))]
        (doseq [{:keys [err]} (nrepl/message client {:op :eval, :code code})]
          (if err
            (main/abort (str/trim err))))))))

(defn bubble
  "Blows bubbles"
  ([project]
   (usage))
  ([project cmd & args]
   (case cmd
     "blow" (apply blow args)
     (usage))))
