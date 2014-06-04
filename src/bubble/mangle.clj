(ns bubble.mangle
  (:require [clojure.walk :as walk]))

(def ^:private names
  (partial map (comp str second first)))

(defn- mangle-sym [sym postfix nss-names]
  (let [sym-ns (namespace sym)]
    (cond
      (and sym-ns (contains? nss-names sym-ns))
      (symbol (str sym-ns \. postfix \/ (name sym)))
      (contains? nss-names (str sym))
      (symbol (str sym \. postfix))
      :else
      sym)))

(defn- mangle-ns
  [postfix nss-names [[_ ns-name :as ns-form] & _ :as forms]]
  {:pre [(= 'ns (first ns-form))
         (symbol? ns-name)]}
  (walk/postwalk #(if (symbol? %)
                    (mangle-sym % postfix nss-names)
                    %)
                 forms))

(defn mangle
  "Given a coll of namespaces' forms return a coll with the postfix appended
  to each qualified symbol or namespace symbol belonging to one of given nss.

  That's not really clear, is it? Consider an example.

    (mangle '[[(ns bubble.core)
               (clojure.core/prn bubble.core/bar)]]
            :postfix 'foo)
    ;; => '[[(ns bubble.core.foo)
    ;;       (clojure.core/prn bubble.core.foo/bar)]]"
  [nss & {:keys [postfix]}]
  (let [nss-names (set (names nss))]
    (map (partial mangle-ns postfix nss-names) nss)))
