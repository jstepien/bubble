(defproject bubble-ring "0.1.0-SNAPSHOT"
  :description "A Ring-based app with code in a Bubble"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [bubble "0.0.0-SNAPSHOT"]
                 [http-kit "2.1.16"]]
  :plugins [[lein-bubble "0.0.0-SNAPSHOT"]]
  :main bubble-ring.main)
